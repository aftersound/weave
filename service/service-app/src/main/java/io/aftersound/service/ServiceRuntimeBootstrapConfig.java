package io.aftersound.service;

import io.aftersound.service.runtime.ConfigFormat;
import io.aftersound.service.runtime.ConfigUpdateStrategy;

import java.util.List;
import java.util.Map;

public class ServiceRuntimeBootstrapConfig {

    private String configFormat;
    private String configUpdateStrategy;
    private Long configUpdateAutoRefreshInterval;
    private String runtimeConfigClass;
    private List<String> componentFactoryTypes;
    private List<Map<String, Object>> componentConfigs;

    public String getConfigFormat() {
        return configFormat;
    }

    public void setConfigFormat(String configFormat) {
        this.configFormat = configFormat;
    }

    public ConfigFormat configFormat() {
        if (ConfigFormat.Yaml.name().equalsIgnoreCase(configFormat)) {
            return ConfigFormat.Yaml;
        } else {
            return ConfigFormat.Json;
        }
    }

    public String getConfigUpdateStrategy() {
        return configUpdateStrategy;
    }

    public void setConfigUpdateStrategy(String configUpdateStrategy) {
        this.configUpdateStrategy = configUpdateStrategy;
    }

    public Long getConfigUpdateAutoRefreshInterval() {
        return configUpdateAutoRefreshInterval;
    }

    public void setConfigUpdateAutoRefreshInterval(Long configUpdateAutoRefreshInterval) {
        this.configUpdateAutoRefreshInterval = configUpdateAutoRefreshInterval;
    }

    public ConfigUpdateStrategy configUpdateStrategy() {
        if ("AutoRefresh".equalsIgnoreCase(configUpdateStrategy)) {
            long autoRefreshInterval = 5000L;
            if (configUpdateAutoRefreshInterval != null && configUpdateAutoRefreshInterval > 0L) {
                autoRefreshInterval = configUpdateAutoRefreshInterval;
            }
            return ConfigUpdateStrategy.autoRefresh(autoRefreshInterval);
        } else {
            return ConfigUpdateStrategy.ondemand();
        }
    }

    public String getRuntimeConfigClass() {
        return runtimeConfigClass;
    }

    public void setRuntimeConfigClass(String runtimeConfigClass) {
        this.runtimeConfigClass = runtimeConfigClass;
    }

    public List<String> getComponentFactoryTypes() {
        return componentFactoryTypes;
    }

    public void setComponentFactoryTypes(List<String> componentFactoryTypes) {
        this.componentFactoryTypes = componentFactoryTypes;
    }

    public List<Map<String, Object>> getComponentConfigs() {
        return componentConfigs;
    }

    public void setComponentConfigs(List<Map<String, Object>> componentConfigs) {
        this.componentConfigs = componentConfigs;
    }

}
