package io.aftersound.component;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ComponentConfigTest {

    @Test
    public void testSimpleComponentConfig() throws Exception {
        SimpleComponentConfig config = new SimpleComponentConfig();
        config.setType("TEST");
        config.setId("id");
        config.setTags(new LinkedHashSet<>(Collections.singletonList("test")));
        Map<String, Object> options = new HashMap<>();
        options.put("host", "localhost");
        config.setOptions(options);

        assertEquals("TEST", config.getType());
        assertEquals("id", config.getId());
        assertNotNull(config.getTags());
        assertTrue(config.getTags().contains("test"));
    }

}