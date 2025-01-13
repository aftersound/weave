package io.aftersound.util.map;

import java.util.Map;

public class Handle {

    private final Map<String, Object> m;

    private Handle(Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("map is null, not acceptable");
        }
        this.m = map;
    }

    public static Handle of(Map<String, Object> map) {
        return new Handle(map);
    }

    public Object get(String path) {
        return Path.of(path).query().on(m);
    }

    public <T> T get(String path, Class<T> type) {
        Object v = get(path);
        return type.isInstance(v) ? type.cast(v) : null;
    }

    public Handle set(String path, Object value) {
        return Path.of(path).update(value).on(m);
    }
}
