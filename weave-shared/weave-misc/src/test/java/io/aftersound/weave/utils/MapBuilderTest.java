package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MapBuilderTest {

    @Test
    public void testBuild() {
        Map<String, String> o = new MapBuilder().option("a", "a1").option("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));
    }
}
