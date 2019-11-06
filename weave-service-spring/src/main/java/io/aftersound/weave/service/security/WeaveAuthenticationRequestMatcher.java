package io.aftersound.weave.service.security;

import io.aftersound.weave.security.AuthenticationControl;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

class WeaveAuthenticationRequestMatcher implements RequestMatcher {

    private final SecurityControlRegistry securityControlRegistry;

    public WeaveAuthenticationRequestMatcher(SecurityControlRegistry securityControlRegistry) {
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        AuthenticationControl authenticationControl = securityControlRegistry.getAuthenticationControl(
                request.getRequestURI()
        );
        return authenticationControl != null;
    }

}
