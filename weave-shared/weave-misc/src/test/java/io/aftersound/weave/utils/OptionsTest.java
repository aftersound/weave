package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OptionsTest {

    @Test
    public void testFrom() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", "b");
        Options o = Options.from(m);
        assertEquals("b", o.get("a"));
    }

    @Test
    public void testFromNull() {
        Options o = Options.from(null);
        assertNull(o.get("a"));
    }

    @Test
    public void testGet() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", "b");
        Options o = Options.from(m);
        assertEquals("b", o.get("a"));
        assertNull(o.get("not_existing_key"));
    }

    @Test(expected = ClassCastException.class)
    public void testGetTypeMismatch() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", "b");
        Options o = Options.from(m);
        Integer value = o.get("a");
        System.out.println(value);
    }

    @Test
    public void testGetWithDefaultValue() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", "a-value");
        Options o = Options.from(m);
        assertEquals("a-value", o.get("a", "a-default-value"));
        assertEquals("d-default-value", o.get("d", "d-default-value"));
    }
}
