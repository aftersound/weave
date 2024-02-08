package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K,V> {

    private final Map<K, V> map;

    private transient K[] keys;

    private MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public static <K, V> MapBuilder<K, V> hashMap() {
        return new MapBuilder<K, V>(new HashMap<>());
    }

    public static <K, V> MapBuilder<K, V> hashMap(Map<? extends K, ? extends V> m) {
        return new MapBuilder<>(new HashMap<>(m));
    }

    public static <K, V> MapBuilder<K, V> hashMap(int initialCapacity) {
        return new MapBuilder<>(new HashMap<>(initialCapacity));
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap() {
        return new MapBuilder<>(new LinkedHashMap<>());
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap(Map<? extends K, ? extends V> m) {
        return new MapBuilder<>(new LinkedHashMap<>(m));
    }

    public static <K, V> MapBuilder<K, V> linkedHashMap(int initialCapacity) {
        return new MapBuilder<>(new LinkedHashMap<>(initialCapacity));
    }

    public MapBuilder put(K k, V v) {
        this.map.put(k, v);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2, K k3, V v3) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        this.map.put(k6, v6);
        return this;
    }

    public MapBuilder put(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        this.map.put(k6, v6);
        this.map.put(k7, v7);
        return this;
    }

    public MapBuilder put(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        this.map.put(k6, v6);
        this.map.put(k7, v7);
        this.map.put(k8, v8);
        return this;
    }

    public MapBuilder put(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        this.map.put(k6, v6);
        this.map.put(k7, v7);
        this.map.put(k8, v8);
        this.map.put(k9, v9);
        return this;
    }

    public MapBuilder put(
            K k1, V v1,
            K k2, V v2,
            K k3, V v3,
            K k4, V v4,
            K k5, V v5,
            K k6, V v6,
            K k7, V v7,
            K k8, V v8,
            K k9, V v9,
            K k10, V v10) {
        this.map.put(k1, v1);
        this.map.put(k2, v2);
        this.map.put(k3, v3);
        this.map.put(k4, v4);
        this.map.put(k5, v5);
        this.map.put(k6, v6);
        this.map.put(k7, v7);
        this.map.put(k8, v8);
        this.map.put(k9, v9);
        this.map.put(k10, v10);
        return this;
    }

    public MapBuilder putIf(K k, V v, boolean condition) {
        if (condition) {
            this.map.put(k, v);
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
