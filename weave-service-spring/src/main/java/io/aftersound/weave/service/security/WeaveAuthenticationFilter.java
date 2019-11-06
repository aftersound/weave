package io.aftersound.weave.service.security;

import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class WeaveAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final SecurityControlRegistry securityControlRegistry;

    public WeaveAuthenticationFilter(SecurityControlRegistry securityControlRegistry) {
        super(new WeaveAuthenticationRequestMatcher(securityControlRegistry));
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        AuthenticationControl authenticationControl = securityControlRegistry.getAuthenticationControl(
                request.getRequestURI()
        );

        Authenticator<HttpServletRequest> authenticator = getAuthenticator(authenticationControl);
        io.aftersound.weave.security.Authentication auth = authenticator.attemptAuthentication(request);
        return auth != null ? new AuthenticationWrapper(auth) : null;
    }

    private Authenticator<HttpServletRequest> getAuthenticator(AuthenticationControl authenticationControl) {
        // TODO: get hold an Authenticator which acts in according to given control
        return new Authenticator<HttpServletRequest>() {

            @Override
            public io.aftersound.weave.security.Authentication attemptAuthentication(
                    HttpServletRequest httpServletRequest) {
                return null;
            }

        };
    }
}
