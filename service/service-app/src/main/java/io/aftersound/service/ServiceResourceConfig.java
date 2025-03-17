package io.aftersound.service;

import io.aftersound.service.jersey.JacksonObjectMapperContextResolver;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerRequestFilter;

@Configuration
@ApplicationPath("/")
public class ServiceResourceConfig extends ResourceConfig {

    @Inject
    @Named("auth-filter")
    ContainerRequestFilter authFilter;

    @Inject
    @Named("rate-limit-filter")
    ContainerRequestFilter rateLimitFilter;

    public ServiceResourceConfig() {
        packages(JacksonObjectMapperContextResolver.class.getPackage().getName());
    }

    @PostConstruct
    public void init() {
        register(authFilter);
        register(rateLimitFilter);
        register(ServiceResource.class);
    }

}
