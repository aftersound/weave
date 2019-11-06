package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

// https://spring.io/guides/topicals/spring-security-architecture/
@Configuration
@EnableWebSecurity
public class WeaveServiceSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityControlRegistry securityControlRegistry;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
         builder.authenticationProvider(new WeaveAuthenticationProvider(securityControlRegistry));
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//         web.privilegeEvaluator(weavePrivilegeEvaluator);
//    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        configureDynamicSecurityViaFilter(http);
    }

    /**
     * Provides some level of dynamic behavior, but once configured, can not change on the fly in according
     * to SecurityControl(s) available in SecurityControlRegistry
     */
    private void configureDynamicSecurityViaRequestMatcher(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.requestMatcher(new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                String requestURI = request.getRequestURI();
                return "/admin/service/extension/list".equals(requestURI);
            }
        }).authorizeRequests().anyRequest().authenticated().and().httpBasic();
    }

    private void configureDynamicSecurityViaExpressionHandler(HttpSecurity httpSecurity) throws Exception {
    }

    private void configureDynamicSecurityViaFilter(HttpSecurity httpSecurity) throws Exception {
        WeaveAuthenticationFilter authenticationFilter = new WeaveAuthenticationFilter(securityControlRegistry);
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        httpSecurity.addFilterAfter(authenticationFilter, BasicAuthenticationFilter.class);

        WeaveAuthorizationFilter weaveAuthorizationFilter = new WeaveAuthorizationFilter(securityControlRegistry);
        httpSecurity.addFilterAfter(weaveAuthorizationFilter, WeaveAuthenticationFilter.class);
    }

}
