package io.aftersound.weave.component;

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
}
