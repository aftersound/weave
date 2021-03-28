package io.aftersound.weave.service;

import io.aftersound.weave.service.jersey.JacksonObjectMapperContextResolver;
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

    public ServiceResourceConfig() {
        packages(JacksonObjectMapperContextResolver.class.getPackage().getName());
    }

    @PostConstruct
    public void init() {
        register(authFilter);
        register(ServiceResource.class);
    }

}
