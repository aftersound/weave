package io.aftersound.weave.service.runtime;

public abstract class ClientAndNamespaceAwareConfigProvider<CLIENT, CONFIG> extends ConfigProvider<CONFIG> {

    protected final CLIENT client;
    protected final String namespace;
    protected final String configIdentifier;
    protected final ConfigFormat configFormat;

    protected ClientAndNamespaceAwareConfigProvider(CLIENT client, String namespace, String configIdentifier, ConfigFormat configFormat) {
        this.client = client;
        this.namespace = namespace;
        this.configIdentifier = configIdentifier;
        this.configFormat = configFormat;
    }

}