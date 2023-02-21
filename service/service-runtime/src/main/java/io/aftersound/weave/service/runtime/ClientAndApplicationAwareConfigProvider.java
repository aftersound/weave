package io.aftersound.weave.service.runtime;

public abstract class ClientAndApplicationAwareConfigProvider<CLIENT, CONFIG> extends ConfigProvider<CONFIG> {

    protected final CLIENT client;
    protected final String namespace;
    protected final String application;
    protected final String configIdentifier;
    protected final ConfigFormat configFormat;

    protected ClientAndApplicationAwareConfigProvider(
            CLIENT client,
            String namespace,
            String application,
            String configIdentifier,
            ConfigFormat configFormat) {
        this.client = client;
        this.namespace = namespace;
        this.application = application;
        this.configIdentifier = configIdentifier;
        this.configFormat = configFormat;
    }

}