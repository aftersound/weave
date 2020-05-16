package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapBuilderTest {

    @Test
    public void testBuild() {
        Map<String, String> o = MapBuilder.hashMap().kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));
    }
}
