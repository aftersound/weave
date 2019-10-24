package io.aftersound.weave.common;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class KVHolder<H extends KVHolder> {

    private final Map<String, Object> kv;

    protected KVHolder() {
        this.kv = new LinkedHashMap<>();
    }

    public <T> H set(Key<T> key, T value) {
        kv.put(key.name(), value);
        return (H) this;
    }

    public <T> T get(Key<T> key) {
        Object v = kv.get(key.name());
        if (key.valueType().isInstance(v)) {
            return key.valueType().cast(v);
        } else {
            return null;
        }
    }

    public Collection<String> keys() {
        return kv.keySet();
    }
}
