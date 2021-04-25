package io.aftersound.weave.component;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ComponentConfigTest {

    @Test
    public void testSimpleComponentConfig() throws Exception {
        SimpleComponentConfig config = new SimpleComponentConfig();
        config.setType("TEST");
        config.setId("id");
        Map<String, String> options = new HashMap<String, String>();
        options.put("host", "localhost");
        config.setOptions(options);

        assertEquals("TEST", config.getType());
        assertEquals("id", config.getId());
    }

}