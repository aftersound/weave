package io.aftersound.config;


import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import io.aftersound.util.Key;

import java.util.*;
import java.util.regex.Pattern;

import static io.aftersound.config.KeyAttributes.*;

public class ConfigUtils {

    private static final boolean ENFORCE_REQUIRED = true;

    /**
     * Get configuration {@link Key}s tagged as SECRET
     *
     * @param keys - a collection of config {@link Key}s whose values are secrets
     * @return a collection of config {@link Key}s whose values are secrets
     */
    public static Collection<Key<?>> getSecretKeys(Collection<Key<?>> keys) {
        List<Key<?>> securityKeys = new ArrayList<>();
        for (Key<?> candidate : keys) {
            if (KeyFilters.SECRET_KEY.isAcceptable(candidate)) {
                securityKeys.add(candidate);
            }
        }
        return securityKeys;
    }

    /**
     * Extract configuration from source
     *
     * @param configSource    - source of configuration in form of {@link Map} of key and value
     * @param keys            - configuration keys of interests
     * @param enforceRequired - whether to enforce required check as specified in the keys
     * @return a configuration represented in form of {@link Map}
     */
    public static Map<String, Object> extractConfig(Map<String, Object> configSource, Collection<Key<?>> keys, boolean enforceRequired) {
        Map<String, Object> config = new HashMap<>();

        List<Key<?>> actualKeys = new ArrayList<>();
        for (Key<?> key : keys) {
            Pattern pattern = key.getAttribute(PATTERN);
            if (pattern != null) {
                for (String keyName : configSource.keySet()) {
                    if (pattern.matcher(keyName).matches()) {
                        Key<?> actualKey = Key.of(keyName, key.type())
                                .bindDefaultValue(key.defaultValue())
                                .bindParseFunc(key.parseFunc())
                                .withAttributes(key.attributes());
                        actualKeys.add(actualKey);
                    }
                }
            } else {
                actualKeys.add(key);
            }
        }

        for (Key<?> key : actualKeys) {
            Func<Map<String, Object>, Object> parseFunc;

            if (key.getAttribute(PATTERN) != null) {
                FuncFactory funcFactory = key.getAttribute(FUNC_FACTORY);
                parseFunc = funcFactory.create(String.format("MAP:GET(%s)", key.name()));
            } else {
                parseFunc = key.parseFunc();
            }

            Object value = parseFunc.apply(configSource);

            // fall back to default value if available
            if (value == null) {
                value = key.defaultValue();
            }

            // required verification
            if (value == null) {
                boolean required = key.getAttribute(REQUIRED, false);
                if (required && enforceRequired) {
                    throw ConfigException.requiredConfigInvalidOrUnspecified(key);
                }
            }

            if (value != null) {
                config.put(key.name(), value);
            }
        }

        return config;
    }

    /**
     * Extract configuration from source
     *
     * @param configSource    - source of configuration in form of {@link Map} of key and value
     * @param keys            - configuration keys of interests
     * @return a configuration represented in form of {@link Map}
     */
    public static Map<String, Object> extractConfig(Map<String, Object> configSource, Collection<Key<?>> keys) {
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

}
