package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.jersey.ClientHandle;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.runtime.*;

public class ServiceBasedRuntimeConfig extends ClientAndApplicationAwareRuntimeConfig<ClientHandle> {

    public ServiceBasedRuntimeConfig(
            ComponentRegistry componentRegistry,
            String clientId,
            String namespace,
            String application,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        super(componentRegistry, clientId, namespace, application, configFormat, configUpdateStrategy);
    }

    @Override
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ServiceBasedActorBindingsConfigProvider(
                client,
                namespace,
                application,
                ConfigIdentifiers.ACTOR_BINDINGS_CONFIG_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ComponentConfig> getComponentConfigProvider() {
        return new ServiceBasedComponentConfigProvider(
                client,
                namespace,
                application,
                ConfigIdentifiers.COMPONENT_CONFIG_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ServiceBasedServiceMetadataProvider(
                client,
                namespace,
                application,
                ConfigIdentifiers.SERVICE_METADATA_LIST,
                configFormat
        );
    }

}
