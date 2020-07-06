package io.aftersound.weave.service.runtime;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.metadata.ServiceMetadata;

public interface RuntimeConfig {

    ServiceInstance getServiceInstance();

    ClientRegistry getBootstrapClientRegistry();

    ConfigFormat getConfigFormat();

    ConfigUpdateStrategy getConfigUpdateStrategy();

    ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider();

    ConfigProvider<Endpoint> getClientConfigProvider();

    ConfigProvider<ServiceMetadata> getServiceMetadataProvider();

    ConfigProvider<ResourceConfig> getResourceConfigProvider();

    ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider();

    ConfigProvider<ResourceConfig> getAdminResourceConfigProvider();

    ConfigProvider<ResourceDeclarationOverride> getAdminResourceDeclarationOverrideConfigProvider();

}
