package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationProperties {

    @Value("${bootstrap.client.factory}")
    private String bootstrapClientFactory;

    @Value("${bootstrap.client.config}")
    private String bootstrapClientConfig;

    @Value("${application.name}")
    private String applicationName;

    @Value("${runtime.config.class}")
    private String runtimeConfigClass;

    @Value("${runtime.namespace}")
    private String namespace;

    @Value("${runtime.environment}")
    private String environment;

    @Value("${runtime.config.format}")
    private String configFormat;

    @Value("${runtime.config.update.strategy}")
    private String configUpdateStrategy;

    @Value("${runtime.config.update.autorefresh.interval}")
    private String configAutoRefreshInternal;

    public String getBootstrapClientFactory() {
        return bootstrapClientFactory;
    }

    public void setBootstrapClientFactory(String bootstrapClientFactory) {
        this.bootstrapClientFactory = bootstrapClientFactory;
    }

    public String getBootstrapClientConfig() {
        return bootstrapClientConfig;
    }

    public void setBootstrapClientConfig(String bootstrapClientConfig) {
        this.bootstrapClientConfig = bootstrapClientConfig;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getRuntimeConfigClass() {
        return runtimeConfigClass;
    }

    public void setRuntimeConfigClass(String runtimeConfigClass) {
        this.runtimeConfigClass = runtimeConfigClass;
    }

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

    public String getConfigFormat() {
        return configFormat;
    }

    public void setConfigFormat(String configFormat) {
        this.configFormat = configFormat;
    }

    public String getConfigUpdateStrategy() {
        return configUpdateStrategy;
    }

    public void setConfigUpdateStrategy(String configUpdateStrategy) {
        this.configUpdateStrategy = configUpdateStrategy;
    }

    public String getConfigAutoRefreshInternal() {
        return configAutoRefreshInternal;
    }

    public void setConfigAutoRefreshInternal(String configAutoRefreshInternal) {
        this.configAutoRefreshInternal = configAutoRefreshInternal;
    }

}
