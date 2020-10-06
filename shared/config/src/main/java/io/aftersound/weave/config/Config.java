package io.aftersound.weave.config;

import io.aftersound.weave.common.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Config {

    private final Map<String, Object> options;
    private final Collection<Key<?>> keys;
    private final Collection<Key<?>> securityKeys;

    private Config(Map<String, Object> options, Collection<Key<?>> configKeys) {
        this.options = options;
        this.keys = configKeys;
        this.securityKeys = ConfigUtils.getSecurityKeys(configKeys);
    }

    public static Config from(Map<String, String> configSource, Collection<Key<?>> keys) {
        return new Config(ConfigUtils.extractConfig(configSource, keys), keys);
    }

    public <T> T v(Key<T> key) {
        return key.valueFrom(options);
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(options);
    }

    public Map<String, String> asMap1() {
        Map<String, String> m = new HashMap<>();
        for (Map.Entry<String, Object> e : options.entrySet()) {
            m.put(e.getKey(), (e.getValue() != null ? String.valueOf(e.getValue()) : null));
        }
        return m;
    }

    public Config subconfig(Collection<Key<?>> subconfigKeys) {
        return new Config(ConfigUtils.extractConfigWithKeys(options, subconfigKeys), subconfigKeys);
    }

}
