package io.aftersound.weave.common;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Container<H extends Container> {

    private final Map<String, Object> kv;

    protected Container() {
        this.kv = new LinkedHashMap<>();
    }

    public <T> H set(Key<T> key, T value) {
        kv.put(key.name(), value);
        return (H) this;
    }

    public <T> T get(Key<T> key) {
        Object v = kv.get(key.name());
        return (T) v;
    }

    public <T> T get(Key<T> key, T defaultValue) {
        Object v = kv.get(key.name());
        return v != null ? (T) v : defaultValue;
    }

    public Collection<String> keys() {
        return kv.keySet();
    }
}
