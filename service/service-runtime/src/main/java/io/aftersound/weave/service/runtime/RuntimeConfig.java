package io.aftersound.weave.service.runtime;

import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;

public interface RuntimeConfig {

    ServiceInstance getServiceInstance();

    ComponentRegistry getBootstrapComponentRegistry();

    ConfigFormat getConfigFormat();

    ConfigUpdateStrategy getConfigUpdateStrategy();

    ExtensionConfigProvider getExtensionConfigProvider();

    ComponentConfigProvider getComponentConfigProvider();

    ServiceMetadataProvider getServiceMetadataProvider();

}
