package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K,V> {

    private Map<K, V> map = new HashMap<>();

    public MapBuilder option(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }

}
