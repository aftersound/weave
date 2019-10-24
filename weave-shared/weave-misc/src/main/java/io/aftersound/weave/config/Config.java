package io.aftersound.weave.config;


public final class Config {

    public static final Config NULL = new Config(ConfigHolder.NULL);

    private ConfigHolder configHolder = ConfigHolder.NULL;

    public Config(ConfigHolder configHolder) {
        if (configHolder != null) {
            this.configHolder = configHolder;
        }
    }

    public String getValue(String configKey) {
        return configHolder.getValue(configKey);
    }

    public <T> T getValue(String configKey, ConfigValueAdaptor<T> configValueAdaptor) {
        if (configValueAdaptor == null) {
            return null;
        }
        return configValueAdaptor.adapt(configHolder.getValue(configKey));
    }

    @Override
    public String toString() {
        return configHolder.toString();
    }
}
