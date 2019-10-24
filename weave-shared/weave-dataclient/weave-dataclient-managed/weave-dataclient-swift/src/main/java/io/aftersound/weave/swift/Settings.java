package io.aftersound.weave.swift;

import io.aftersound.weave.utils.Options;

import java.util.Map;

class Settings {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String AUTH_URL = "authUrl";
    private static final String TENANT_ID = "tenantId";
    private static final String TENANT_NAME = "tenantName";
    private static final String PREFERRED_REGION = "preferredRegion";
    private static final String SSL_VALIDATION_DISABLED = "sslValidationDisabled";
    private static final String SOCKET_TIMEOUT = "socketTimeout";
    private static final String MOCK_ENABLED = "mockEnabled";

    private final Options options;

    private Settings(Options options) {
        this.options = options;
    }

    static Settings from(Map<String, Object> options) {
        return new Settings(Options.from(options));
    }

    public String username() {
        return options.get(USERNAME);
    }

    public String password() {
        return options.get(PASSWORD);
    }

    public String authUrl() {
        return options.get(AUTH_URL);
    }

    public String tenantId() {
        return options.get(TENANT_ID);
    }

    public String tenantName() {
        return options.get(TENANT_NAME);
    }

    public String preferredRegion() {
        return options.get(PREFERRED_REGION);
    }

    public boolean sslValidationDisabled() {
        String disabledOrNot = options.get(SSL_VALIDATION_DISABLED);
        return Boolean.valueOf(disabledOrNot).booleanValue();
    }

    public int socketTimeout() {
        String toValue = options.get(SOCKET_TIMEOUT);
        int timeout = 15000;
        try {
            timeout = Integer.valueOf(toValue);
        } catch (Exception e) {
            // DO NOTHING
        }
        return timeout;
    }

    public boolean mockEnabled() {
        String enabledOrNot = options.get(MOCK_ENABLED);
        return Boolean.valueOf(enabledOrNot).booleanValue();
    }
}
