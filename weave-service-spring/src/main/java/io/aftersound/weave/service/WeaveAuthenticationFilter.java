package io.aftersound.weave.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WeaveAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static class WeaveAuthenticationRequestMatcher implements RequestMatcher {

        private final SecurityControlRegistry securityControlRegistry;

        public WeaveAuthenticationRequestMatcher(SecurityControlRegistry securityControlRegistry) {
            this.securityControlRegistry = securityControlRegistry;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            // TODO: check if authentication is needed for given request based on SecurityControl in
            //       securityControlRegistry
            return false;
        }

    }

    private final SecurityControlRegistry securityControlRegistry;

    public WeaveAuthenticationFilter(SecurityControlRegistry securityControlRegistry) {
        super(new WeaveAuthenticationRequestMatcher(securityControlRegistry));
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // TODO: authenticate request based on SecurityControl in SecurityControl in securityControlRegistry
        return null;
    }
}
