package io.aftersound.weave.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ConfigUtils {

    /**
     * Lock {@link Key}s declared in the dictionary class to make them immutable
     * @param configKeyDictionaryClass
     * @throws IllegalAccessException
     */
    public static void lockDictionary(Class<?> configKeyDictionaryClass) throws IllegalAccessException {
        List<Key<?>> keys = new ArrayList<>();
        for (Field field : configKeyDictionaryClass.getDeclaredFields()) {
            if (field.getType() == Key.class &&
                    (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC &&
                    (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                field.setAccessible(true);
                Key<?> key = (Key<?>)field.get(null);

                // lock to make it immutable
                key.lock();
            }
        }
    }

    /**
     * Get declared {@link Key}s from given config key dictionary class
     * @param configKeyDictionaryClass
     *          - config key dictionary class
     * @param keyFilter
     *          - filter of {@link Key} needs to be included
     * @return list of {@link Key} which matches the filter condition
     * @throws IllegalAccessException
     */
    public static List<Key<?>> getDeclaredKeys(
            Class<?> configKeyDictionaryClass,
            KeyFilter keyFilter) throws IllegalAccessException {

        List<Key<?>> keys = new ArrayList<>();
        for (Field field : configKeyDictionaryClass.getDeclaredFields()) {
            if (field.getType() == Key.class &&
                    (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC &&
                    (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                field.setAccessible(true);
                Key<?> key = (Key<?>)field.get(null);

                if (keyFilter.isAcceptable(key)) {
                    keys.add(key);
                }
            }
        }

        return Collections.unmodifiableList(keys);
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
