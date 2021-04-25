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

    Signature configSignature() {
        return config.signature();
    }

    COMPONENT component() {
        return component;
    }

    ComponentConfig config() {
        return config;
    }

    ComponentConfig maskedConfig() {
        if (config instanceof SimpleComponentConfig) {
            SimpleComponentConfig scc = (SimpleComponentConfig) config;
            Map<String, String> maskedOptions = new LinkedHashMap<>(scc.getOptions());
            for (Key<?> key : ConfigUtils.getSecurityKeys(configKeys)) {
                if (maskedOptions.containsKey(key.name())) {
                    maskedOptions.put(key.name(), "********");
                }
            }
            return SimpleComponentConfig.of(scc.getType(), scc.getId(), maskedOptions);
        } else {
            return config;
        }
    }

    Collection<Key<?>> configKeys() {
        return configKeys;
    }
}
