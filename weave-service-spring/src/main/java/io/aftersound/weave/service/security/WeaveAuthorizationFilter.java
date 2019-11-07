package io.aftersound.weave.service.security;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

class WeaveAuthorizationFilter extends GenericFilterBean {

    private final SecurityControlRegistry securityControlRegistry;
    private final AuthorizerFactory authorizerFactory;

    public WeaveAuthorizationFilter(
            SecurityControlRegistry securityControlRegistry,
            AuthorizerFactory authorizerFactory) {
        this.securityControlRegistry = securityControlRegistry;
        this.authorizerFactory = authorizerFactory;
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
//        chain.doFilter(request, response);
    }

}
