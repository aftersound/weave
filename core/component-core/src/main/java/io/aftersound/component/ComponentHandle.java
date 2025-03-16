package io.aftersound.component;

import io.aftersound.config.ConfigUtils;
import io.aftersound.util.Key;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class ComponentHandle<COMPONENT> {

    private final COMPONENT component;
    private final ComponentConfig config;
    private final Signature configSignature;
    private final Collection<Key<?>> configKeys;

    private ComponentHandle(
            COMPONENT component,
            ComponentConfig config,
            Signature configSignature,
            Collection<Key<?>> configKeys) {
        this.component = component;
        this.config = config;
        this.configSignature = configSignature;
        this.configKeys = configKeys;
    }

    static <COMPONENT> ComponentHandle<COMPONENT> of(
            COMPONENT component,
            ComponentConfig config,
            Signature configSignature,
            Collection<Key<?>> configKeys) {
        return new ComponentHandle<>(component, config, configSignature, configKeys);
    }

    Signature configSignature() {
        return configSignature;
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
            Map<String, Object> maskedOptions = new LinkedHashMap<>(scc.getOptions());
            for (Key<?> key : ConfigUtils.getSecretKeys(configKeys)) {
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
