package io.aftersound.weave.security;

import java.util.HashMap;
import java.util.Map;

public class SecurityConfigRegistry {

    private final Map<String, SecurityConfig> registry;

    public static final SecurityConfigRegistry INSTANCE = new SecurityConfigRegistry();

    private SecurityConfigRegistry() {
        this.registry = new HashMap<>();
    }

    public SecurityConfigRegistry register(String id, SecurityConfig securityControl) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id could not be null or empty");
        }

        if (securityControl == null) {
            throw new IllegalArgumentException("securityControl could not be null");
        }

        if (securityControl.getSettings() == null || securityControl.getSettings().isEmpty()) {
            throw new IllegalArgumentException("securityControl.settings could not be null or empty");
        }

        if (registry.containsKey(id)) {
            throw new IllegalArgumentException(id + " already exists in registry");
        }

        registry.put(id, securityControl);

        return this;
    }

    public SecurityConfig get(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        return registry.get(id);
    }

}
