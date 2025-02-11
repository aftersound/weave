package io.aftersound.config;


import io.aftersound.util.Key;

import java.util.*;

public final class Config {

    private final Map<String, Object> options;
    private final Collection<Key<?>> keys;
    private final Collection<Key<?>> secretKeys;

    private Config(Map<String, Object> options, Collection<Key<?>> configKeys) {
        this.options = options;
        this.keys = configKeys;
        this.secretKeys = ConfigUtils.getSecretKeys(configKeys);
    }

    public static Config from(Map<String, Object> configSource, Collection<Key<?>> keys) {
        return new Config(ConfigUtils.extractConfig(configSource, keys), keys);
    }

    public <T> T v(Key<T> key) {
        T value = key.valueFrom(options);
        return value != null ? value : key.defaultValue();
    }

    public <T> T vRequired(Key<T> key) {
        T value = v(key);
        if (value != null) {
            return value;
        }
        throw ConfigException.requiredConfigInvalidOrUnspecified(key);
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(options);
    }

    public Properties asProperties() {
        Properties properties = new Properties();
        for (Map.Entry<String, Object> e : options.entrySet()) {
            properties.setProperty(e.getKey(), e.getValue() != null ? String.valueOf(e.getValue()) : null);
        }
        return properties;
    }

    public Config subconfig(Collection<Key<?>> subconfigKeys) {
        return new Config(ConfigUtils.extractConfigWithKeys(options, subconfigKeys), subconfigKeys);
    }

}
