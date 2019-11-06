package io.aftersound.weave.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.WebInvocationPrivilegeEvaluator;

class WeavePrivilegeEvaluator implements WebInvocationPrivilegeEvaluator {

    private final SecurityControlRegistry securityControlRegistry;

    WeavePrivilegeEvaluator(SecurityControlRegistry securityControlRegistry) {
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public boolean isAllowed(String uri, Authentication authentication) {
        return false;
    }

    @Override
    public boolean isAllowed(String contextPath, String uri, String method, Authentication authentication) {
        return false;
    }

}
