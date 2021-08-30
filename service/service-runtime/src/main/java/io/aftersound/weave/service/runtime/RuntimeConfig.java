package io.aftersound.weave.service.runtime;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.metadata.ServiceMetadata;

public interface RuntimeConfig {

    ServiceInstance getServiceInstance();

    ComponentRegistry getBootstrapComponentRegistry();

    ConfigFormat getConfigFormat();

    ConfigUpdateStrategy getConfigUpdateStrategy();

    ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider();

    ConfigProvider<ComponentConfig> getComponentConfigProvider();

    ConfigProvider<ServiceMetadata> getServiceMetadataProvider();

    ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider();

}
