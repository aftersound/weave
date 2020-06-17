package io.aftersound.weave.config;

import io.aftersound.weave.common.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Settings {

    private final Map<String, Object> options;

    private Settings(Map<String, Object> options) {
        this.options = options;
    }

    public static Settings from(Map<String, Object> options) {
        return new Settings(options != null ? options : Collections.<String, Object>emptyMap());
    }

    public static Settings from(Map<String, String> rawOptions, Collection<Key<?>> configKeys) {
        return Settings.from(ConfigUtils.extractConfig(rawOptions, configKeys));
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

    public Settings subsettings(Collection<Key<?>> keys) {
        return Settings.from(ConfigUtils.extractConfigWithKeys(options, keys));
    }

}
