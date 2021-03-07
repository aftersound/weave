package io.aftersound.weave.config;

import io.aftersound.weave.common.Key;

import java.util.*;
import java.util.regex.Pattern;

public class ConfigUtils {

    private static final boolean ENFORCE_REQUIRED = true;

    /**
     * Get configuration {@link Key}s tagged as security related
     *
     * @param keys - a collection of config {@link Key}s which might contain security related keys
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
     *
     * @param configSource    - source of configuration in form of {@link Map} of key and value of String type
     * @param keys            - configuration keys of interests
     * @param enforceRequired - whether to enforce required check as specified in the keys
     * @return a configuration represented in form of {@link Map}
     */
    public static Map<String, Object> extractConfig(Map<String, String> configSource, Collection<Key<?>> keys, boolean enforceRequired) {
        Map<String, Object> config = new HashMap<>();

        for (Key<?> key : findKeysWithPattern(keys)) {
            Pattern pattern = key.pattern();
            for (Map.Entry<String, String> e : configSource.entrySet()) {
                if (pattern.matcher(e.getKey()).matches()) {
                    Map<String, String> rawValues = new HashMap<>();
                    rawValues.put(e.getKey(), e.getValue());
                    Object value = key.valueParser().rawKeys(Arrays.asList(e.getKey())).parse(rawValues);
                    if (value != null) {
                        config.put(e.getKey(), value);
                    }
                }
            }
        }

        for (Key<?> key : findKeysWithoutPattern(keys)) {
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
                if (key.isRequired() && enforceRequired) {
                    throw ConfigException.requiredConfigInvalidOrUnspecified(key);
                }
            }
        }

        return config;
    }

    /**
     * Extract configuration from source
     *
     * @param configSource    - source of configuration in form of {@link Map} of key and value of String type
     * @param keys            - configuration keys of interests
     * @return a configuration represented in form of {@link Map}
     */
    public static Map<String, Object> extractConfig(Map<String, String> configSource, Collection<Key<?>> keys) {
        return extractConfig(configSource, keys, ENFORCE_REQUIRED);
    }

    /**
     * Extract config from source config
     *
     * @param sourceConfig - source config which contains configuration values in interests
     * @param configKeys   - keys of configuration values in interests
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

    private static Collection<Key<?>> findKeysWithPattern(Collection<Key<?>> keys) {
        Collection<Key<?>> keysWithPattern = new ArrayList<>();
        for (Key<?> key : keys) {
            if (KeyFilters.KEY_WITH_PATTERN.isAcceptable(key)) {
                keysWithPattern.add(key);
            }
        }
        return keysWithPattern;
    }

    private static Collection<Key<?>> findKeysWithoutPattern(Collection<Key<?>> keys) {
        Collection<Key<?>> keysWithoutPattern = new ArrayList<>();
        for (Key<?> key : keys) {
            if (KeyFilters.KEY_WITHOUT_PATTERN.isAcceptable(key)) {
                keysWithoutPattern.add(key);
            }
        }
        return keysWithoutPattern;
    }

}
