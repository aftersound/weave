package io.aftersound.weave.component;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ComponentConfigTest {

    @Test
    public void testComponentConfig() throws Exception {
        ComponentConfig config = new ComponentConfig();
        config.setType("TEST");
        config.setId("id");
        Map<String, String> options = new HashMap<String, String>();
        options.put("host", "localhost");
        config.setOptions(options);

        assertEquals("TEST", config.getType());
        assertEquals("id", config.getId());
    }

}