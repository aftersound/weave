package io.aftersound.weave.config;

import io.aftersound.weave.common.Key;

import java.util.*;

public class ConfigUtils {

    /**
     * Get configuration {@link Key}s tagged as security related
     * @param keys
     *          - a collection of config {@link Key}s which might contain security related keys
     * @return a collection of config {@link Key}s which are security related
     */
    public static Collection<Key<?>> getSecurityKeys(Collection<Key<?>> keys) {
        List<Key<?>> securityKeys = new ArrayList<>();
        for (Key<?> candidate : keys) {
            if (KeyFilters.SECURITY_KEY.isAcceptable(candidate)) {
                securityKeys.add(candidate);
            }
        }
        return securityKeys;
    }

    /**
     * Extract configuration from source
     * @param configSource
     *          - source of configuration in form of {@link Map} of key and value of String type
     * @param keys
     *          - configuration keys of interests
     * @return a configuration represented in form of {@link Map}
     */
    public static Map<String, Object> extractConfig(Map<String, String> configSource, Collection<Key<?>> keys) {
        Map<String, Object> config = new HashMap<>();

        // include all sources
        //  1.in typical cases, source values will be overridden with parsed values
        //  2.in some cases, source values will not be overridden but be passed through
        for (Map.Entry<String, String> e : configSource.entrySet()) {
            config.put(e.getKey(), e.getValue());
        }

        for (Key<?> key : keys) {
            Map<String, String> rawValues = new HashMap<>();
            for (String rawKey : key.rawKeys()) {
                String rawValue = configSource.get(rawKey);
                if (rawValue != null) {
                    rawValues.put(rawKey, rawValue);
                }
            }
            Object value = key.valueParser().rawKeys(key.rawKeys()).parse(rawValues);
            if (value != null) {
                config.put(key.name(), value);
            } else {
                if (key.isRequired()) {
                    throw ConfigException.requiredConfigInvalidOrUnspecified(key);
                }
            }
        }
        return config;
    }

    /**
     * Extract config from source config
     * @param sourceConfig
     *          - source config which contains configuration values in interests
     * @param configKeys
     *          - keys of configuration values in interests
     * @return config extracted from source config
     */
    public static Map<String, Object> extractConfigWithKeys(Map<String, Object> sourceConfig, Collection<Key<?>> configKeys) {
        Map<String, Object> targetConfig = new HashMap<>();
        for (Key<?> key : configKeys) {
            Object value = sourceConfig.get(key.name());
            if (value != null) {
                targetConfig.put(key.name(), value);
            }
        }
        return targetConfig;
    }

}
