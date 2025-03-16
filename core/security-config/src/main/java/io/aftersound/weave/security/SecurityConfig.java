package io.aftersound.weave.security;

import io.aftersound.common.NamedType;
import io.aftersound.metadata.Control;

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
    private Map<String, String> options;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}