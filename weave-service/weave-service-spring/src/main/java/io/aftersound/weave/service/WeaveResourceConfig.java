package io.aftersound.weave.service;

import io.aftersound.weave.service.jersey.JacksonObjectMapperContextResolver;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/")
public class WeaveResourceConfig extends ResourceConfig {

    public WeaveResourceConfig() {
        packages(JacksonObjectMapperContextResolver.class.getPackage().getName());
    }

    @PostConstruct
    public void init() {
        register(WeaveServiceResource.class);
    }

}
