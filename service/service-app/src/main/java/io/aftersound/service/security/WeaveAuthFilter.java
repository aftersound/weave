package io.aftersound.service.security;

import io.aftersound.actor.ActorRegistry;
import io.aftersound.msg.Message;
import io.aftersound.msg.Severity;
import io.aftersound.service.message.Category;
import io.aftersound.util.MapBuilder;
import jakarta.inject.Named;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import javax.annotation.Priority;
import java.util.Collections;
import java.util.Map;

@Named("auth-filter")
@Priority(Priorities.AUTHORIZATION)
@Provider
public class WeaveAuthFilter implements ContainerRequestFilter, ContainerResponseFilter {

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
        AuthControl authControl = authControlRegistry.getAuthControl(requestContext.getMethod(), requestContext.getUriInfo().getPath());

        // auth is not required
        if (authControl == null) {
            return;
        }

        AuthHandler authHandler = authHandlerRegistry.get(authControl.getType());
        if (authHandler == null) {
            io.aftersound.service.security.SecurityException securityException = io.aftersound.service.security.SecurityException.noAuthHandler(authControl.getType());
            requestContext.abortWith(createAuthHandlingExceptionResponse(requestContext, securityException));
            return;
        }

        try {
            Auth auth = authHandler.handle(
                    authControl,
                    requestContext
            );
            requestContext.setProperty("AUTH", auth);

        } catch (io.aftersound.service.security.SecurityException e) {
            requestContext.abortWith(createAuthHandlingExceptionResponse(requestContext, e));
        }
    }

    private Response createAuthHandlingExceptionResponse(
            ContainerRequestContext requestContext, SecurityException securityException) {

        final int status;

        final Message error = new Message();
        error.setSeverity(Severity.ERROR);
        error.setContent(securityException.getMessage());

        io.aftersound.service.security.SecurityException.Code code = securityException.getCode();
        switch (code) {
            case BadCredential:
            case BadToken:
            case CredentialsExpired:
            case MissingTokenOrCredential:
            case NoAuthHandler:
            case TokenExpired: {
                status = 401;
                error.setCategory(Category.REQUEST.name());
                error.setCode("401");
                break;
            }
            case AccessDenied: {
                status = 403;
                error.setCategory(Category.REQUEST.name());
                error.setCode("403");
                break;
            }
            default: {
                status = 500;
                error.setCategory(Category.SYSTEM.name());
                error.setCode("500");
                break;
            }
        }

        Map<String, Object> errorResponseEntity = MapBuilder.<String, Object>hashMap()
                .put("messages", Collections.singleton(error))
                .build();

        return Response
                .status(status)
                .type(getExpectedResponseMediaType(requestContext))
                .entity(errorResponseEntity)
                .build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    }

    private MediaType getExpectedResponseMediaType(ContainerRequestContext requestContext) {
        String accept = requestContext.getHeaderString("Accept");
        if (accept != null) {
            try {
                return MediaType.valueOf(accept);
            } catch (Exception e) {
                return MediaType.APPLICATION_JSON_TYPE;
            }
        } else {
            return MediaType.APPLICATION_JSON_TYPE;
        }
    }
}
