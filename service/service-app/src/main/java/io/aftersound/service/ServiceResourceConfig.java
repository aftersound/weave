package io.aftersound.service;

import io.aftersound.service.jersey.JacksonObjectMapperContextResolver;
import jakarta.inject.Named;
import jakarta.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ServiceResourceConfig extends ResourceConfig {

    private final ContainerRequestFilter authFilter;
    private final ContainerRequestFilter rateLimitFilter;

    public ServiceResourceConfig(@Named("auth-filter") ContainerRequestFilter authFilter, @Named("rate-limit-filter")ContainerRequestFilter rateLimitFilter) {
        packages(JacksonObjectMapperContextResolver.class.getPackage().getName());
        this.authFilter = authFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @PostConstruct
    public void init() {
        register(authFilter);
        register(rateLimitFilter);
        register(ServiceResource.class);
    }

}
