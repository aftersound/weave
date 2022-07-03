package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K,V> {

    private final Map<K, V> map;

    private K[] keys;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> hashMap() {
        return new MapBuilder(new HashMap());
    }

    public static <K, V> MapBuilder<K, V> hashMap(int initialCapacity) {
        return new MapBuilder(new HashMap(initialCapacity));
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap() {
        return new MapBuilder(new LinkedHashMap());
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap(int initialCapacity) {
        return new MapBuilder(new LinkedHashMap(initialCapacity));
    }

    public MapBuilder kv(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    public MapBuilder keys(K... keys) {
        this.keys = keys;
        return this;
    }

    public MapBuilder values(V... values) {
        assert values != null && values.length > 0 : "given values is null or empty";
        assert keys != null && keys.length > 0 : "keys is null or empty";
        assert keys.length == values.length : "length mismatch between keys and values";

        for (int i = 0; i < keys.length; i++) {
            this.map.put(keys[i], values[i]);
        }

        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(this.map);
    }

    public Map<K, V> buildModifiable() {
        return this.map;
    }

}
