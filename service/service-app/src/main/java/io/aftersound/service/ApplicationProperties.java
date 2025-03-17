package io.aftersound.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {

    @Value("${namespace}")
    private String namespace;

    @Value("${application}")
    private String application;

    @Value("${environment}")
    private String environment;

    @Value("${bootstrap.config}")
    private String bootstrapConfig;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBootstrapConfig() {
        return bootstrapConfig;
    }

    public void setBootstrapConfig(String bootstrapConfig) {
        this.bootstrapConfig = bootstrapConfig;
    }
}
