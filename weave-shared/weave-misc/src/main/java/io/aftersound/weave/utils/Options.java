package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class Options {

    private final Map<String, Object> optionsAsMap;

    private Options(Map<String, Object> optionsAsMap) {
        this.optionsAsMap = optionsAsMap != null ? optionsAsMap : Collections.<String, Object>emptyMap();
    }

    public static Options from(Map<String, Object> optionsAsMap) {
        return new Options(optionsAsMap);
    }

    public Set<String> keys() {
        return optionsAsMap.keySet();
    }

    @SuppressWarnings("unchecked")
    public <V> V get(String key) {
        return this.get(key, null);
    }

    @SuppressWarnings("unchecked")
    public <V> V get(String key, V defaultValue) {
        Object v = optionsAsMap.get(key);
        if (v != null) {
            return (V)v;
        } else {
            return defaultValue;
        }
    }

}
