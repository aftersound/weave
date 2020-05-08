package io.aftersound.weave.client;

import io.aftersound.weave.config.ConfigUtils;
import io.aftersound.weave.config.Key;
import io.aftersound.weave.security.SecurityConfig;
import io.aftersound.weave.security.SecurityConfigProvider;
import io.aftersound.weave.security.SecurityConfigProviderRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ClientConfigUtils {

    private static final boolean CONFIG_BY_REFERENCE_ENABLED = Boolean.parseBoolean(
            System.getProperty("config.by.reference.enabled")
    );

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
    public static Map<String, Object> extractConfig(
            Map<String, String> sourceConfig,
            Collection<Key<?>> configKeys,
            Collection<Key<?>> securityKeys) {

        Map<String, Object> config = new HashMap<>();

        // Extract config from source
        config.putAll(ConfigUtils.extractConfig(sourceConfig, configKeys));

        // Extract security control related config
        if (CONFIG_BY_REFERENCE_ENABLED) {
            // Extract security control related config from configuration as pointed by reference
            String securityControlId = sourceConfig.get("security.control");
            if (securityControlId != null) {
                SecurityConfigProvider provider = SecurityConfigProviderRegistry.getInstance().getProvider();
                SecurityConfig securityControl = provider.get(securityControlId);
                if (securityControl == null) {
                    throw new IllegalStateException("No SecurityControl with id " + securityControlId + " is found");
                }
                config.putAll(ConfigUtils.extractConfig(securityControl.getOptions(), securityKeys));
            }
        } else {
            // Extract security control related config from source config
            config.putAll(ConfigUtils.extractConfig(sourceConfig, securityKeys));
        }

        return config;
    }

}
