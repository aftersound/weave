package io.aftersound.weave.component;

import io.aftersound.weave.metadata.Control;

import java.util.Map;

/**
 * Component config of general form
 */
public class ComponentConfig implements Control {

    private String type;
    private String id;
    private Map<String, String> options;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public static ComponentConfig of(String type, String id, Map<String, String> options) {
        ComponentConfig e = new ComponentConfig();
        e.setType(type);
        e.setId(id);
        e.setOptions(options);
        return e;
    }

}
