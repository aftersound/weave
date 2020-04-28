package io.aftersound.weave.config;

public interface ConfigHolder {

    String getValue(String key);

    public static final ConfigHolder NULL = new ConfigHolder() {
        @Override
        public String getValue(String key) {
            return null;
        }
    };
}
