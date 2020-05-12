package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.ConcurrentSessionFilter;

@Configuration
@EnableWebSecurity
public class WeaveServiceSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityControlRegistry securityControlRegistry;

    private final ActorRegistry<Authenticator> authenticatorRegistry;

    private final ActorRegistry<Authorizer> authorizerRegistry;

    public WeaveServiceSecurityConfig(
            SecurityControlRegistry securityControlRegistry,
            ActorRegistry<Authenticator> authenticatorRegistry,
            ActorRegistry<Authorizer> authorizerRegistry) {
        this.securityControlRegistry = securityControlRegistry;
        this.authenticatorRegistry = authenticatorRegistry;
        this.authorizerRegistry = authorizerRegistry;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        WeaveAuthFilter authFilter = new WeaveAuthFilter(
                securityControlRegistry,
                authenticatorRegistry,
                authorizerRegistry
        );

        // hook authentication filter
        http.addFilterAfter(authFilter, ConcurrentSessionFilter.class);
        http.csrf().disable();
    }

}
