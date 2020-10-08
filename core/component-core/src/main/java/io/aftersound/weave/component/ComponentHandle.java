package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.config.ConfigUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class ComponentHandle<COMPONENT> {

    private final COMPONENT component;
    private final ComponentConfig config;
    private final Collection<Key<?>> configKeys;

    private ComponentHandle(COMPONENT component, ComponentConfig config, Collection<Key<?>> configKeys) {
        this.component = component;
        this.config = config;
        this.configKeys = configKeys;
    }

    static <COMPONENT> ComponentHandle of(COMPONENT component, ComponentConfig config, Collection<Key<?>> configKeys) {
        return new ComponentHandle(component, config, configKeys);
    }

    int optionsHash() {
        return config.getOptions().hashCode();
    }

    COMPONENT component() {
        return component;
    }

    ComponentConfig config() {
        return config;
    }

    ComponentConfig maskedConfig() {
        Map<String, String> maskedOptions = new LinkedHashMap<>(config.getOptions());
        for (Key<?> key : ConfigUtils.getSecurityKeys(configKeys)) {
            if (maskedOptions.containsKey(key.name())) {
                maskedOptions.put(key.name(), "********");
            }
        }
        return ComponentConfig.of(config.getType(), config.getId(), maskedOptions);
    }

    Collection<Key<?>> configKeys() {
        return configKeys;
    }
}
