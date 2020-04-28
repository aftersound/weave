package io.aftersound.weave.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {

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
                    throw ConfigException.requiredConfigInvalidOrUnspecified(key.name());
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
