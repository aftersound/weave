package io.aftersound.weave.service.runtime;

import io.aftersound.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;

public interface RuntimeConfig {

    ServiceInstance getServiceInstance();

    ComponentRegistry getBootstrapComponentRegistry();

    ConfigFormat getConfigFormat();

    ConfigUpdateStrategy getConfigUpdateStrategy();

    ConfigProvider getConfigProvider();

}
