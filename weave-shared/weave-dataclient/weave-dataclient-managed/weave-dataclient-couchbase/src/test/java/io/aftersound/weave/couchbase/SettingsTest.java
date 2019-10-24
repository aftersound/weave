package io.aftersound.weave.couchbase;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SettingsTest {

    @Test
    public void testSettings() {
        Map<String, Object> options = new HashMap<>();
        options.put("nodes", "192.168.1.1,192.168.1.2,192.168.1.3");
        options.put("bucket", "TEST");
        options.put("username", "USER");
        options.put("password", "PASSWORD");

        Settings settings = Settings.from(options);
        assertNotNull(settings.getNodes());
        assertEquals("192.168.1.1", settings.getNodes()[0]);
        assertEquals("TEST", settings.getBucket());
        assertEquals("USER", settings.getUsername());
        assertEquals("PASSWORD", settings.getPassword());
    }

}