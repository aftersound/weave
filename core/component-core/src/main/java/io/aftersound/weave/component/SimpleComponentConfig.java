package io.aftersound.weave.component;

import io.aftersound.config.Config;
import io.aftersound.util.Key;

import java.util.Collection;
import java.util.Map;

public final class SimpleComponentConfig extends ComponentConfig {

    private Map<String, String> options;

    public static SimpleComponentConfig of(String type, String id, Map<String, String> options) {
        SimpleComponentConfig c = new SimpleComponentConfig();
        c.setType(type);
        c.setId(id);
        c.setOptions(options);
        return c;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    private transient Collection<Key<?>> configKeys;

    /**
     * Parsed {@link Config} object with strong typed configuration entries
     */
    private transient Config config;

    protected SimpleComponentConfig configKeys(Collection<Key<?>> configKeys) {
        if (this.configKeys == null) {
            this.configKeys = configKeys;
            this.config = Config.from(options, configKeys);
        }
        return this;
    }

    protected Config config() {
        return config;
    }

}
