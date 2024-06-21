package io.aftersound.weave.utils;

import java.util.Map;

public class MapAccessor {

    private final Map<String, Object> m;

    public MapAccessor(Map<String, Object> m) {
        this.m = m;
    }

    public <T> T query(String pathQuery) {
        return MapQuery.parse(pathQuery).on(m);
    }

}
