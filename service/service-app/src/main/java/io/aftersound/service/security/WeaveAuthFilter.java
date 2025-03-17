package io.aftersound.service.security;

import io.aftersound.msg.Message;
import io.aftersound.msg.Severity;
import io.aftersound.service.security.*;
import io.aftersound.service.security.SecurityException;
import io.aftersound.util.MapBuilder;
import io.aftersound.actor.ActorRegistry;
import io.aftersound.service.message.Category;

import javax.annotation.Priority;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
            io.aftersound.service.security.SecurityException securityException = io.aftersound.service.security.SecurityException.noAuthHandler(authControl.getType());
            requestContext.abortWith(createAuthHandlingExceptionResponse(securityException));
            return;
        }

        try {
            Auth auth = authHandler.handle(
                    authControl,
                    request
            );
            requestContext.setProperty("AUTH", auth);

        } catch (io.aftersound.service.security.SecurityException e) {
            requestContext.abortWith(createAuthHandlingExceptionResponse(e));
        }
    }

    private Response createAuthHandlingExceptionResponse(SecurityException securityException) {
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
                .type(getExpectedResponseMediaType(request))
                .entity(errorResponseEntity)
                .build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    }

    private MediaType getExpectedResponseMediaType(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
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
