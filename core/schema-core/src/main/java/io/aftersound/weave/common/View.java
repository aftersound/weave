package io.aftersound.weave.common;

import java.util.Map;

public class View {

    /**
     * name of this view
     */
    private String name;

    /**
     * schema of view
     */
    private Schema schema;

    /**
     * additional but optional multi-purpose configuration
     */
    private Map<String, String> config;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
