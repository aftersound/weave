package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

// https://spring.io/guides/topicals/spring-security-architecture/
@Configuration
@EnableWebSecurity
public class WeaveServiceSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    WeaveAuthenticationProvider weaveAuthenticationProvider;

    @Autowired
    WeavePrivilegeEvaluator weavePrivilegeEvaluator;

//    @Override
//    public void configure(AuthenticationManagerBuilder builder) throws Exception {
//         builder.authenticationProvider(weaveAuthenticationProvider);
//    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//         web.privilegeEvaluator(weavePrivilegeEvaluator);
//    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        /* below snippet has conditional behavior
        http.requestMatcher(new RequestMatcher() {
            @Override
            public boolean matches(HttpServletRequest request) {
                String requestURI = request.getRequestURI();
                return "/admin/service/extension/list".equals(requestURI);
            }
        }).authorizeRequests().anyRequest().authenticated().and().httpBasic();
        */
//        httpSecurity.authenticationProvider(weaveAuthenticationProvider);
//        http.authorizeRequests().expressionHandler().
//        http.authorizeRequests().anyRequest().authenticated()
//                .and().httpBasic();
    }

}
