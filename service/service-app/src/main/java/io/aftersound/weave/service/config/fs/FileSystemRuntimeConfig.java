package io.aftersound.weave.service.config.fs;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.client.fs.FileSystem;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.config.BaseRuntimeConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.runtime.*;

public class FileSystemRuntimeConfig extends BaseRuntimeConfig<FileSystem> {

    public FileSystemRuntimeConfig(
            ClientRegistry clientRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        super(clientRegistry, clientId, namespace, configFormat, configUpdateStrategy);
    }

    @Override
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new FileSystemActorBindingsConfigProvider(
                client,
                namespace,
                ConfigIdentifiers.ACTOR_BINDINGS_CONFIG_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<Endpoint> getClientConfigProvider() {
        return new FileSystemClientConfigProvider(
                client,
                namespace,
                ConfigIdentifiers.ENDPOINT_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new FileSystemServiceMetadataProvider(
                client,
                namespace,
                ConfigIdentifiers.SERVICE_METADATA_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ResourceConfig> getResourceConfigProvider() {
        return new FileSystemResourceConfigProvider(
                client,
                namespace,
                ConfigIdentifiers.RESOURCE_CONFIG_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider() {
        return new FileSystemServiceMetadataProvider(
                client,
                namespace,
                ConfigIdentifiers.ADMIN_SERVICE_METADATA_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ResourceConfig> getAdminResourceConfigProvider() {
        return new FileSystemResourceConfigProvider(
                client,
                namespace,
                ConfigIdentifiers.ADMIN_RESOURCE_CONFIG_LIST,
                configFormat
        );
    }

    @Override
    public ConfigProvider<ResourceDeclarationOverride> getAdminResourceDeclarationOverrideConfigProvider() {
        return new FileSystemResourceDeclarationOverrideProvider(
                client,
                namespace,
                ConfigIdentifiers.ADMIN_RESOURCE_DECLARATION_OVERRIDE_LIST,
                configFormat
        );
    }
}
