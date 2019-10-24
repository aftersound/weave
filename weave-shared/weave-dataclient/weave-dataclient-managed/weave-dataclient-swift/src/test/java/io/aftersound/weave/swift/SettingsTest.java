package io.aftersound.weave.swift;

import io.aftersound.weave.utils.OptionsBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SettingsTest {

    @Test
    public void testSettings() {
        Map<String, Object> options = new OptionsBuilder()
                .option("tenantId", "test")
                .option("tenantName", "test")
                .option("authUrl", "http://localhost:5000/v2.0/tokens")
                .option("username", "test")
                .option("password", "password")
                .option("preferredRegion", "Region1")
                .option("sslValidationDisabled", "true")
                .option("mockEnabled", "true")
                .build();

        Settings settings = Settings.from(options);
        assertEquals("test", settings.tenantId());
        assertEquals("test", settings.tenantName());
        assertEquals("http://localhost:5000/v2.0/tokens", settings.authUrl());
        assertEquals("test", settings.username());
        assertEquals("password", settings.password());
        assertEquals("Region1", settings.preferredRegion());
        assertTrue(settings.sslValidationDisabled());
        assertTrue(settings.mockEnabled());
    }
}