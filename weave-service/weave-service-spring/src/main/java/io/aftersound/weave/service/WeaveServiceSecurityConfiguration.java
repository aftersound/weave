package io.aftersound.weave.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

// https://spring.io/guides/topicals/spring-security-architecture/
@Configuration
public class WeaveServiceSecurityConfiguration extends WebSecurityConfigurerAdapter {

    // DelegateAuthenticationProvider delegateAuthenticationProvider;

    // SecurityPolicyRegistry securityPolicyRegistry;

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        // TODO:
        //   bind customized authentication providers
        // builder.authenticationProvider(delegateAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // TODO:
        //  create a customized privilege evaluator
        //  bind customized privilege evaluator which acts in according to security policy in registry
        // web.privilegeEvaluator()
    }

}
