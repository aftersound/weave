package io.aftersound.weave.service.security;

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
        // TODO: authenticate request based on SecurityControl in SecurityControl in securityControlRegistry
        return null;
    }
}
