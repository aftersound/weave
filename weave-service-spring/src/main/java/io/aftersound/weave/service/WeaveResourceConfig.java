package io.aftersound.weave.service;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/")
public class WeaveResourceConfig extends ResourceConfig {

    @PostConstruct
    public void init() {
        register(WeaveServiceResource.class);
    }

}
