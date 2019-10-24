package io.aftersound.weave.utils;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OptionsBuilderTest {

    @Test
    public void testBuild() {
        Map<String, Object> o = new OptionsBuilder().option("a", "a1").option("b", "b1").build();
        assertEquals("a1", o.get("a"));
        assertEquals("b1", o.get("b"));
        assertNull(o.get("c"));
    }
}
