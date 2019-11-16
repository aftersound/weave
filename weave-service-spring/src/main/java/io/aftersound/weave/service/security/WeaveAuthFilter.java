package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.security.SecurityException;
import io.aftersound.weave.security.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class WeaveAuthFilter extends GenericFilterBean {

    private final SecurityControlRegistry securityControlRegistry;
    private final ActorRegistry<Authenticator> authenticatorRegistry;
    private final ActorRegistry<Authorizer> authorizerRegistry;

    public WeaveAuthFilter(
            SecurityControlRegistry securityControlRegistry,
            ActorRegistry<Authenticator> authenticatorRegistry,
            ActorRegistry<Authorizer> authorizerRegistry) {
        this.securityControlRegistry = securityControlRegistry;
        this.authenticatorRegistry = authenticatorRegistry;
        this.authorizerRegistry = authorizerRegistry;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        AuthenticationWrapper authenticationWrapper = doAuthNAuth(req);
        if (authenticationWrapper != null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationWrapper);
        }

        chain.doFilter(request, response);
    }

    private AuthenticationWrapper doAuthNAuth(HttpServletRequest request) {
        AuthenticationControl authenticationControl = securityControlRegistry.getAuthenticationControl(
                request.getRequestURI()
        );

        // authentication is not required
        if (authenticationControl == null) {
            return null;
        }

        Authenticator authenticator = authenticatorRegistry.get(authenticationControl.getType());
        if (authenticator == null) {
            throw new AuthenticationServiceException("No authentication service for " + authenticationControl.getType());
        }

        io.aftersound.weave.security.Authentication auth;
        try {
            auth = authenticator.authenticate(
                    authenticationControl,
                    request
            );
        } catch (SecurityException securityException) {
            SecurityException.Code code = securityException.getCode();
            switch (code) {
                case MissingTokenOrCredential:
                case BadToken:
                case BadCredential:
                    throw new BadCredentialsException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
                case TokenExpired:
                case CredentialsExpired:
                    throw new CredentialsExpiredException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
                case AuthenticationServiceError:
                    throw new AuthenticationServiceException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
                default:
                    throw new UnclassifiedSecurityException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
            }
        } catch (Exception other) {
            throw new AuthenticationServiceException("Authentication service exception", other);
        }

        AuthorizationControl authorizationControl = securityControlRegistry.getAuthorizationControl(
                request.getRequestURI()
        );

        // no authorization check is required
        if (authorizationControl == null) {
            return new AuthenticationWrapper(authenticationControl, auth);
        }

        Authorizer authorizer = authorizerRegistry.get(authorizationControl.getType());
        if (authorizer == null) {
            throw new AuthorizationServiceException("No authorization service for " + authorizationControl.getType());
        }

        io.aftersound.weave.security.Authorization authNAuth;
        try {
            authNAuth = authorizer.authorize(authorizationControl, auth);
        } catch (SecurityException securityException) {
            switch (securityException.getCode()) {
                case AccessDenied:
                    throw new AccessDeniedException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
                default:
                    throw new UnclassifiedSecurityException(
                            securityException.getMessage(),
                            securityException.getCause()
                    );
            }
        } catch (Exception other) {
            throw new AuthorizationServiceException("Authorization service exception", other);
        }

        return new AuthenticationWrapper(authenticationControl, authNAuth);
    }

}
