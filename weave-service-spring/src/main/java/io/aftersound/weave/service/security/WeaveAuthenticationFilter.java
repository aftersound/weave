package io.aftersound.weave.service.security;

import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class WeaveAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final SecurityControlRegistry securityControlRegistry;
    private final AuthenticatorFactory authenticatorFactory;

    public WeaveAuthenticationFilter(
            SecurityControlRegistry securityControlRegistry,
            AuthenticatorFactory authenticatorFactory) {
        super(new WeaveAuthenticationRequestMatcher(securityControlRegistry));
        this.securityControlRegistry = securityControlRegistry;
        this.authenticatorFactory = authenticatorFactory;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        AuthenticationControl authenticationControl = securityControlRegistry.getAuthenticationControl(
                request.getServletPath()
        );

        Authenticator authenticator = authenticatorFactory.getAuthenticator(authenticationControl);
        io.aftersound.weave.security.Authentication auth = authenticator.attemptAuthentication(
                authenticationControl,
                request.getHeader("Authorization")
        );
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return new AuthenticationWrapper(auth);
    }

}
