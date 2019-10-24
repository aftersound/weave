package io.aftersound.weave.dataclient;

import java.util.Map;

/**
 * Conceptual entity that captures everything about connecting to
 * endpoint of data source.
 */
public final class Endpoint {

    private String type;
    private String id;
    private Map<String, Object> options;

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

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

}
