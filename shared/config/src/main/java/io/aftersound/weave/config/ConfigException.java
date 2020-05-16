package io.aftersound.weave.config;

public class ConfigException extends RuntimeException {

    private ConfigException(String msg) {
        super(msg);
    }

    public static ConfigException requiredConfigInvalidOrUnspecified(Key<?> configKey) {
        return new ConfigException("required config " + configKey.name() + " is invalid or not specified");
    }

    public static ConfigException create(String message) {
        return new ConfigException(message);
    }
}
