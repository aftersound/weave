package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K,V> {

    private final Map<K, V> map;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> hashMap() {
        return new MapBuilder(new HashMap());
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap() {
        return new MapBuilder(new LinkedHashMap());
    }

    public MapBuilder kv(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(this.map);
    }

}
