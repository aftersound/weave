package io.aftersound.weave.util.map;

import java.util.HashMap;
import java.util.Map;

public class Directives {

    private Map<Key<?>, Object> d;

    public Directives() {
        this.d = new HashMap<>();
    }

    public <T> Directives set(Key<T> key, T value) {
        this.d.put(key, value);
        return this;
    }

    public <T> T get(Key<T> key) {
        Object v = d.get(key);
        return key.type().cast(v);
    }

    public boolean isEmpty() {
        return d.isEmpty();
    }

    public Directives acquire(Directives others) {
        if (others != null) {
            this.d.putAll(others.d);
        }
        return this;
    }

}
