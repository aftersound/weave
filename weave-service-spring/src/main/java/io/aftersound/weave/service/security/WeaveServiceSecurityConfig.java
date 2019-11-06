package io.aftersound.weave.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
         builder.authenticationProvider(new WeaveAuthenticationProvider(securityControlRegistry));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // hook authentication filter
        WeaveAuthenticationFilter authenticationFilter = new WeaveAuthenticationFilter(securityControlRegistry);
        authenticationFilter.setAuthenticationManager(authenticationManager);
        http.addFilterAfter(authenticationFilter, BasicAuthenticationFilter.class);

        // hook authorization filter
        WeaveAuthorizationFilter weaveAuthorizationFilter = new WeaveAuthorizationFilter(securityControlRegistry);
        http.addFilterAfter(weaveAuthorizationFilter, WeaveAuthenticationFilter.class);
    }

}
