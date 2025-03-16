package io.aftersound.weave.service.runtime;

import io.aftersound.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;

public abstract class ClientAndApplicationAwareRuntimeConfig<CLIENT> implements RuntimeConfig {

    private final ComponentRegistry componentRegistry;

    protected final String clientId;
    protected final CLIENT client;
    protected final String namespace;
    protected final String application;
    protected final ConfigFormat configFormat;
    protected final ConfigUpdateStrategy configUpdateStrategy;

    private ServiceInstance serviceInstance;

    protected ClientAndApplicationAwareRuntimeConfig(
            ComponentRegistry componentRegistry,
            String clientId,
            String namespace,
            String application,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        this.componentRegistry = componentRegistry;
        this.clientId = clientId;
        this.client = componentRegistry.getComponent(clientId);
        this.namespace = namespace;
        this.application = application;
        this.configFormat = configFormat;
        this.configUpdateStrategy = configUpdateStrategy;
    }

    public void setServiceInstance(ServiceInstance serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public final ServiceInstance getServiceInstance() {
        return serviceInstance;
    }

    @Override
    public final ConfigFormat getConfigFormat() {
        return configFormat;
    }

    @Override
    public final ConfigUpdateStrategy getConfigUpdateStrategy() {
        return configUpdateStrategy;
    }

    @Override
    public final ComponentRegistry getBootstrapComponentRegistry() {
        return componentRegistry;
    }

}