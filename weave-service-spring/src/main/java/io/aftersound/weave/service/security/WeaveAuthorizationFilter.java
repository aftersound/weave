package io.aftersound.weave.service.security;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

class WeaveAuthorizationFilter extends GenericFilterBean {

    private final SecurityControlRegistry securityControlRegistry;

    public WeaveAuthorizationFilter(SecurityControlRegistry securityControlRegistry) {
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }

}
