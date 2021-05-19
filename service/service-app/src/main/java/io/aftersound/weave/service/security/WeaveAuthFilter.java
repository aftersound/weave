package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.message.Category;
import io.aftersound.weave.service.message.Message;
import io.aftersound.weave.service.message.Severity;
import io.aftersound.weave.utils.MapBuilder;

import javax.annotation.Priority;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.Map;

@Named("auth-filter")
@Priority(Priorities.AUTHORIZATION)
@Provider
public class WeaveAuthFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    protected HttpServletRequest request;

    private final AuthControlRegistry authControlRegistry;
    private final ActorRegistry<AuthHandler> authHandlerRegistry;

    public WeaveAuthFilter(
            AuthControlRegistry authControlRegistry,
            ActorRegistry<AuthHandler> authHandlerRegistry) {
        this.authControlRegistry = authControlRegistry;
        this.authHandlerRegistry = authHandlerRegistry;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        AuthControl authControl = authControlRegistry.getAuthControl(request.getMethod(), request.getRequestURI());

        // auth is not required
        if (authControl == null) {
            return;
        }

        AuthHandler authHandler = authHandlerRegistry.get(authControl.getType());
        if (authHandler == null) {
            SecurityException securityException = SecurityException.noAuthHandler(authControl.getType());
            requestContext.abortWith(createAuthHandlingExceptionResponse(securityException));
            return;
        }

        try {
            Auth auth = authHandler.handle(
                    authControl,
                    request
            );
            requestContext.setProperty("AUTH", auth);

        } catch (SecurityException e) {
            requestContext.abortWith(createAuthHandlingExceptionResponse(e));
        }
    }

    private Response createAuthHandlingExceptionResponse(SecurityException securityException) {
        final int status;

        final Message error = new Message();
        error.setSeverity(Severity.ERROR);
        error.setMessage(securityException.getMessage());

        SecurityException.Code code = securityException.getCode();
        switch (code) {
            case BadCredential:
            case BadToken:
            case CredentialsExpired:
            case MissingTokenOrCredential:
            case NoAuthHandler:
            case TokenExpired: {
                status = 401;
                error.setCategory(Category.REQUEST);
                error.setId(401L);
                break;
            }
            case AccessDenied: {
                status = 403;
                error.setCategory(Category.REQUEST);
                error.setId(403L);
                break;
            }
            default: {
                status = 500;
                error.setCategory(Category.SYSTEM);
                error.setId(500L);
                break;
            }
        }

        Map<String, Object> errorResponseEntity = MapBuilder.hashMap()
                .kv("messages", Collections.singleton(error))
                .build();
        return Response.status(status).entity(errorResponseEntity).build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    }
}
