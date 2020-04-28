package io.aftersound.weave.config;

public class ConfigException extends RuntimeException {

    private ConfigException(String msg) {
        super(msg);
    }

    public static ConfigException requiredConfigInvalidOrUnspecified(String configKey) {
        return new ConfigException("required config " + configKey + " is invalid or not specified");
    }
}
