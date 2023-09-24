package io.aftersound.weave.common;

import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class ChainOfResponsibilityValueFuncTest {

    @Test
    public void apply() {
        ValueFunc<Map<String, String>, String> cor = new ChainOfResponsibilityValueFunc<>(
                Arrays.asList(
                        new MapGetFunc("k1"),
                        new MapGetFunc("k2"),
                        new MapGetFunc("k3"),
                        new MapGetFunc("k4")
                )
        );
        String v = cor.apply(MapBuilder.hashMap().kv("k3", "v3").build());
        assertEquals("v3", v);

        v = cor.apply(MapBuilder.hashMap().kv("k5", "v5").build());
        assertNull(v);
    }

    private static class MapGetFunc extends ValueFunc<Map<String, String>, String> {

        private final String key;

        public MapGetFunc(String key) {
            this.key = key;
        }

        @Override
        public String apply(Map<String, String> source) {
            return source != null ? source.get(key) : null;
        }
    }

}