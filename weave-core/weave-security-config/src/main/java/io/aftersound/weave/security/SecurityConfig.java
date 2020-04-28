package io.aftersound.weave.security;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.metadata.Control;

import java.util.Map;

public class SecurityConfig implements Control {

    public static final NamedType<SecurityConfig> TYPE = NamedType.of(
            "SecurityConfig",
            SecurityConfig.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String id;
    private Map<String, String> settings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }
}