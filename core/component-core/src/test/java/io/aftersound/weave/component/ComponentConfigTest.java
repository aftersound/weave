package io.aftersound.weave.component;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import static org.junit.Assert.*;

public class ComponentConfigTest {

    @Test
    public void testSimpleComponentConfig() throws Exception {
        SimpleComponentConfig config = new SimpleComponentConfig();
        config.setType("TEST");
        config.setId("id");
        config.setTags(new LinkedHashSet<>(Arrays.asList("test")));
        Map<String, String> options = new HashMap<String, String>();
        options.put("host", "localhost");
        config.setOptions(options);

        assertEquals("TEST", config.getType());
        assertEquals("id", config.getId());
        assertNotNull(config.getTags());
        assertTrue(config.getTags().contains("test"));
    }

}