package io.aftersound.weave.dataclient;

import io.aftersound.weave.config.ConfigUtils;
import io.aftersound.weave.config.Key;
import io.aftersound.weave.security.SecurityConfig;
import io.aftersound.weave.security.SecurityConfigRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DataClientConfigUtils {

    /**
     * Extract config from given source config
     *
     * @param sourceConfig
     *          - source config in a map
     * @param configKeys
     *          - keys of non-security-related configuration to be extracted
     * @param securityKeys
     *          - keys of security-related configuration to be extracted
     *
     * @return configuration extracted from given source config
     */
    public static Map<String, Object> extractConfig(Map<String, String> sourceConfig, Collection<Key<?>> configKeys, Collection<Key<?>> securityKeys) {
        Map<String, Object> config = new HashMap<>();

        // Extract config from source
        config.putAll(ConfigUtils.extractConfig(sourceConfig, configKeys));

        // Extract security control related config
        String securityControlId = sourceConfig.get("security.control");
        if (securityControlId != null) {
            SecurityConfig securityControl = SecurityConfigRegistry.INSTANCE.get(securityControlId);
            if (securityControl == null) {
                throw new IllegalArgumentException("No SecurityControl with id " + securityControlId + " is not registered");
            }
            config.putAll(ConfigUtils.extractConfig(securityControl.getSettings(), securityKeys));
        }

        return config;
    }

}
