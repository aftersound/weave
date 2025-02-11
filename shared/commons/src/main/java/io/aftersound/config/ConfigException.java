package io.aftersound.config;

import io.aftersound.util.Key;

public class ConfigException extends RuntimeException {

    private ConfigException(String msg) {
        super(msg);
    }

    public static ConfigException requiredConfigInvalidOrUnspecified(Key<?> configKey) {
        return new ConfigException("required config '" + configKey.name() + "' is invalid or not specified");
    }

    public static ConfigException create(String message) {
        return new ConfigException(message);
    }
}
