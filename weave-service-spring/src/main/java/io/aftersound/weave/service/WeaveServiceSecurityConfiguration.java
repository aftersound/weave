package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// https://spring.io/guides/topicals/spring-security-architecture/
@Configuration
public class WeaveServiceSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    WeaveAuthenticationProvider weaveAuthenticationProvider;

    @Autowired
    WeavePrivilegeEvaluator weavePrivilegeEvaluator;

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(weaveAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.privilegeEvaluator(weavePrivilegeEvaluator);
    }

}
