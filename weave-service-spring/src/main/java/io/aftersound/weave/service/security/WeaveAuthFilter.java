package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.security.SecurityException;
import io.aftersound.weave.security.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WeaveAuthFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveAuthFilter.class);

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

        AuthenticationWrapper authenticationWrapper;
        try {
            authenticationWrapper = doAuthNAuth(req);
        } catch (SecurityException e) {
            LOGGER.error("{} occurred when attempting to authenticate", e);
            handleSecurityExceptionResponse(response, e);
            return;
        } catch (Throwable e) {
            LOGGER.error("{} occurred when attempting to authenticate", e);
            handleSecurityExceptionResponse(response, SecurityException.unclassifiable(e));
            return;
        }

        if (authenticationWrapper != null) {
            SecurityContextHolder.getContext().setAuthentication(authenticationWrapper);
        }

        chain.doFilter(request, response);
    }

    private AuthenticationWrapper doAuthNAuth(HttpServletRequest request) throws SecurityException {
        AuthenticationControl authenticationControl = securityControlRegistry.getAuthenticationControl(
                request.getRequestURI()
        );

        // authentication is not required
        if (authenticationControl == null) {
            return null;
        }

        Authenticator authenticator = authenticatorRegistry.get(authenticationControl.getType());
        if (authenticator == null) {
            throw SecurityException.noAuthenticator(authenticationControl.getType());
        }

        io.aftersound.weave.security.Authentication auth = authenticator.authenticate(
                authenticationControl,
                request
        );


        AuthorizationControl authorizationControl = securityControlRegistry.getAuthorizationControl(
                request.getRequestURI()
        );

        // no authorization check is required
        if (authorizationControl == null) {
            return new AuthenticationWrapper(authenticationControl, auth);
        }

        Authorizer authorizer = authorizerRegistry.get(authorizationControl.getType());
        if (authorizer == null) {
            throw SecurityException.noAuthorizer(authorizationControl.getType());
        }

        io.aftersound.weave.security.Authorization authNAuth = authorizer.authorize(authorizationControl, auth);

        return new AuthenticationWrapper(authenticationControl, authNAuth);
    }

    private void handleSecurityExceptionResponse(ServletResponse response, SecurityException e) throws IOException {
        String content;
        SecurityException.Code code = e.getCode();
        switch (code) {
            case MissingTokenOrCredential:
                content = SecurityErrorResponses.MISSING_TOKEN_OR_CREDENTIAL;
                break;
            case BadToken:
                content = SecurityErrorResponses.BAD_TOKEN;
                break;
            case BadCredential:
                content = SecurityErrorResponses.BAD_CREDENTIAL;
                break;
            case TokenExpired:
                content = SecurityErrorResponses.TOKEN_EXPIRED;
                break;
            case CredentialsExpired:
                content = SecurityErrorResponses.CREDENTIAL_EXPIRED;
                break;
            case AuthenticationServiceError:
                content = SecurityErrorResponses.AUTHENTICATION_SERVICE_ERROR;
                break;
            case AccessDenied:
                content = SecurityErrorResponses.ACCESS_DENIED;
                break;
            default:
                content = SecurityErrorResponses.UNCLASSIFIED_SECURITY_ERROR;
        }

        if (response instanceof HttpServletResponse) {
            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        response.setContentType("application/json");
        response.getWriter().println(content);
    }
}
