package io.aftersound.weave.component;

import java.util.Map;

public final class SimpleComponentConfig implements ComponentConfig {

    private String type;
    private String id;
    private Map<String, String> options;

    public static SimpleComponentConfig of(String type, String id, Map<String, String> options) {
        SimpleComponentConfig c = new SimpleComponentConfig();
        c.setType(type);
        c.setId(id);
        c.setOptions(options);
        return c;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    @Override
    public Signature signature() {
        return new SimpleSignature(this);
    }
}
