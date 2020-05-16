package io.aftersound.weave.client;

import java.util.Map;

/**
 * Conceptual entity that captures everything about connecting to
 * endpoint of data source.
 */
public final class Endpoint {

    private String type;
    private String id;
    private Map<String, String> options;

    public static Endpoint of(String type, String id, Map<String, String> options) {
        Endpoint e = new Endpoint();
        e.setType(type);
        e.setId(id);
        e.setOptions(options);
        return e;
    }

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

}
