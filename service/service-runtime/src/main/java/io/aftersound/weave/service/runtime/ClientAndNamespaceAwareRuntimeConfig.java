package io.aftersound.weave.service.runtime;

import io.aftersound.weave.client.ClientRegistry;

public abstract class ClientAndNamespaceAwareRuntimeConfig<CLIENT> implements RuntimeConfig {

    private final ClientRegistry clientRegistry;

    protected final CLIENT client;
    protected final String namespace;
    protected final ConfigFormat configFormat;
    protected final ConfigUpdateStrategy configUpdateStrategy;

    protected ClientAndNamespaceAwareRuntimeConfig(
            ClientRegistry clientRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        this.clientRegistry = clientRegistry;
        this.client = clientRegistry.getClient(clientId);
        this.namespace = namespace;
        this.configFormat = configFormat;
        this.configUpdateStrategy = configUpdateStrategy;
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
    public final ClientRegistry getBootstrapClientRegistry() {
        return clientRegistry;
    }

}