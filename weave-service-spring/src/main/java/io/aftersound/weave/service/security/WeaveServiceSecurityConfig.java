package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.security.Authentication;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import io.aftersound.weave.security.Authorization;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.security.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

// https://spring.io/guides/topicals/spring-security-architecture/
@Configuration
@EnableWebSecurity
public class WeaveServiceSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    SecurityControlRegistry securityControlRegistry;

    @Autowired
    ActorBindings<AuthenticationControl, Authenticator, Authentication> authenticatorBindings;

    @Autowired
    ActorBindings<AuthorizationControl, Authorizer, Authorization> authorizerBindings;

//    @Override
//    public void configure(AuthenticationManagerBuilder builder) throws Exception {
//         builder.authenticationProvider(new WeaveAuthenticationProvider(securityControlRegistry));
//    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
//        // hook authentication filter
//        WeaveAuthenticationFilter authenticationFilter = new WeaveAuthenticationFilter(
//                securityControlRegistry,
//                new AuthenticatorFactory(authenticatorBindings)
//        );
//        authenticationFilter.setAuthenticationManager(authenticationManager);
//        http.addFilterAfter(authenticationFilter, BasicAuthenticationFilter.class);
//
//        // hook authorization filter
//        WeaveAuthorizationFilter weaveAuthorizationFilter = new WeaveAuthorizationFilter(
//                securityControlRegistry,
//                new AuthorizerFactory(authorizerBindings)
//        );
//        http.addFilterAfter(weaveAuthorizationFilter, WeaveAuthenticationFilter.class);
//
//        http.formLogin().disable();
    }

}
