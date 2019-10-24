package io.aftersound.weave.utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionsBuilder {

    private Map<String, Object> options = new LinkedHashMap<>();

    public OptionsBuilder option(String key, Object value) {
        options.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return Collections.unmodifiableMap(options);
    }

}
