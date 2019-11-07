package io.aftersound.weave.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

@Configuration
@EnableWebSecurity
public class WeaveServiceSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityControlRegistry securityControlRegistry;

    @Autowired
    AuthenticatorFactory authenticatorFactory;

    @Autowired
    AuthorizerFactory authorizerFactory;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        WeaveAuthFilter authFilter = new WeaveAuthFilter(
                securityControlRegistry,
                authenticatorFactory,
                authorizerFactory
        );

        // hook authentication filter
        http.addFilterAfter(authFilter, ConcurrentSessionFilter.class);
    }

}
