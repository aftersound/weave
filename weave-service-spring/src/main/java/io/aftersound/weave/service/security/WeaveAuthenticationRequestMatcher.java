package io.aftersound.weave.service.security;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

class WeaveAuthenticationRequestMatcher implements RequestMatcher {

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
