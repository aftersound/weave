package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MapHandlerTest {

    @Test
    public void hashMap() {
        MapHandler h = new MapHandler(new HashMap<>());
        h.setValueAtPath("Nikola", "name", "first");
        h.setValueAtPath("Tesla", "name", "last");

        assertEquals("Nikola", h.getValueAtPath("name", "first"));
        assertEquals("Tesla", h.getValueAtPath("name", "last"));

        Map<String, Object> m = h.get();
        assertTrue(m.get("name") instanceof HashMap);
        Map<String, Object> name = (Map<String, Object>) m.get("name");
        assertEquals("Nikola", name.get("first"));
        assertEquals("Tesla", name.get("last"));
    }

    @Test
    public void treeMap() {
        MapHandler h = new MapHandler(new TreeMap<>());
        h.setValueAtPath("Nikola", "name", "first");
        h.setValueAtPath("Tesla", "name", "last");

        assertEquals("Nikola", h.getValueAtPath("name", "first"));
        assertEquals("Tesla", h.getValueAtPath("name", "last"));

        Map<String, Object> m = h.get();
        assertTrue(m.get("name") instanceof TreeMap);
        Map<String, Object> name = (Map<String, Object>) m.get("name");
        assertEquals("Nikola", name.get("first"));
        assertEquals("Tesla", name.get("last"));
    }

    @Test
    public void linkedHashMap() {
        MapHandler h = new MapHandler(new LinkedHashMap<>());
        h.setValueAtPath("Nikola", "name", "first");
        h.setValueAtPath("Tesla", "name", "last");

        assertEquals("Nikola", h.getValueAtPath("name", "first"));
        assertEquals("Tesla", h.getValueAtPath("name", "last"));

        Map<String, Object> m = h.get();
        assertTrue(m.get("name") instanceof LinkedHashMap);
        Map<String, Object> name = (Map<String, Object>) m.get("name");
        assertEquals("Nikola", name.get("first"));
        assertEquals("Tesla", name.get("last"));
    }

}