package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class MapBuilderTest {

    @Test
    public void hashMap() {
        final Map<String, String> m1 = MapBuilder.hashMap().kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", m1.get("a"));
        assertEquals("b1", m1.get("b"));
        assertNull(m1.get("c"));
        assertThrows(
                Exception.class,
                () -> m1.put("c", "c1")
        );

        final Map<String, String> m2 = MapBuilder.hashMap().kv("a", "a1").kv("b", "b1").buildModifiable();
        m2.put("c", "c1");
        assertEquals("a1", m2.get("a"));
        assertEquals("b1", m2.get("b"));
        assertEquals("c1", m2.get("c"));

        final Map<String, String> m3 = MapBuilder.hashMap(2).kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", m3.get("a"));
        assertEquals("b1", m3.get("b"));
        assertNull(m3.get("c"));

        final Map<String, String> m4 = MapBuilder.hashMap(m3).put("c", "c1").build();
        assertEquals("a1", m4.get("a"));
        assertEquals("b1", m4.get("b"));
        assertEquals("c1", m4.get("c"));

        final Map<String, String> m5 = MapBuilder.hashMap(m4).putIf("c", "c2", m4.containsKey("c")).putIf("d", "d1", m4.containsKey("d")).build();
        assertEquals("c2", m5.get("c"));
        assertFalse(m5.containsKey("d"));
    }

    @Test
    public void linkedHashMap() {
        final Map<String, String> m1 = MapBuilder.linkedHashMap().kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", m1.get("a"));
        assertEquals("b1", m1.get("b"));
        assertNull(m1.get("c"));
        assertThrows(
                Exception.class,
                () -> m1.put("c", "c1")
        );

        final Map<String, String> m2 = MapBuilder.linkedHashMap().kv("a", "a1").kv("b", "b1").buildModifiable();
        m2.put("c", "c1");
        assertEquals("a1", m2.get("a"));
        assertEquals("b1", m2.get("b"));
        assertEquals("c1", m2.get("c"));

        final Map<String, String> m3 = MapBuilder.linkedHashMap(2).kv("a", "a1").kv("b", "b1").build();
        assertEquals("a1", m3.get("a"));
        assertEquals("b1", m3.get("b"));
        assertNull(m3.get("c"));

        final Map<String, String> m4 = MapBuilder.linkedHashMap(m3).put("c", "c1").build();
        assertEquals("a1", m4.get("a"));
        assertEquals("b1", m4.get("b"));
        assertEquals("c1", m4.get("c"));

        final Map<String, String> m5 = MapBuilder.linkedHashMap(m4).putIf("c", "c2", m4.containsKey("c")).putIf("d", "d1", m4.containsKey("d")).build();
        assertEquals("c2", m5.get("c"));
        assertFalse(m5.containsKey("d"));
    }
}
