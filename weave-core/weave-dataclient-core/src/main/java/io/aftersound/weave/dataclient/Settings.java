package io.aftersound.weave.dataclient;

import io.aftersound.weave.config.ConfigUtils;
import io.aftersound.weave.config.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class Settings {

    private final Map<String, Object> options;

    private Settings(Map<String, Object> options) {
        this.options = options;
    }

    public static Settings from(Map<String, Object> options) {
        return new Settings(options != null ? options : Collections.<String, Object>emptyMap());
    }

    public <T> T get(Key<T> key) {
        return key.valueFrom(options);
    }

    public Map<String, Object> get(Collection<Key<?>> keys) {
        return ConfigUtils.extractConfigWithKeys(options, keys);
    }
}
