package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {

    @Value("${runtime.namespace}")
    private String namespace;

    @Value("${runtime.environment}")
    private String environment;

    @Value("${runtime.bootstrap.config}")
    private String runtimeBootstrapConfig;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getRuntimeBootstrapConfig() {
        return runtimeBootstrapConfig;
    }

    public void setRuntimeBootstrapConfig(String runtimeBootstrapConfig) {
        this.runtimeBootstrapConfig = runtimeBootstrapConfig;
    }

}
