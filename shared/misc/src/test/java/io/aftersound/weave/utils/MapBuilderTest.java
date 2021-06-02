package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

public class MapBuilderTest {

    @Test
    public void hashMap1() {
        Map<String, String> o;

        o = MapBuilder.hashMap().kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));

        try {
            o.put("c", "c1");
            fail();
        } catch (Exception e) {
        }

        o = MapBuilder.hashMap().kv("a", "a1").kv("b", "b1").buildModifiable();
        o.put("c", "c1");

        o = MapBuilder.hashMap(2).kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));
    }

    @Test
    public void linkedHashMap() {
        Map<String, String> o;

        o = MapBuilder.linkedHashMap().kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));

        try {
            o.put("c", "c1");
            fail();
        } catch (Exception e) {
        }

        o = MapBuilder.linkedHashMap().kv("a", "a1").kv("b", "b1").buildModifiable();
        o.put("c", "c1");

        o = MapBuilder.linkedHashMap(2).kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));
    }
}
