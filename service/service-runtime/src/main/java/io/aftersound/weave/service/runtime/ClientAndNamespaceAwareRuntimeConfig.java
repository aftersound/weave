package io.aftersound.weave.service.runtime;

import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;

public abstract class ClientAndNamespaceAwareRuntimeConfig<CLIENT> implements RuntimeConfig {

    private final ComponentRegistry componentRegistry;

    protected final CLIENT client;
    protected final String namespace;
    protected final ConfigFormat configFormat;
    protected final ConfigUpdateStrategy configUpdateStrategy;

    private ServiceInstance serviceInstance;

    protected ClientAndNamespaceAwareRuntimeConfig(
            ComponentRegistry componentRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        this.componentRegistry = componentRegistry;
        this.client = componentRegistry.getComponent(clientId);
        this.namespace = namespace;
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