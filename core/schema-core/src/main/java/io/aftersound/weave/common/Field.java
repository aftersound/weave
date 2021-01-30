package io.aftersound.weave.common;

import java.util.Map;

/**
 * Conceptual field in a record
 */
public class Field {

    /**
     * name of this field
     */
    private String name;

    /**
     * The specification of field value regarding how to get and parse value in
     * a record. The format o the specification is largely determined in the
     * context of component which references it.
     */
    private String valueSpec;

    /**
     * additional but optional multi-purpose configuration
     */
    private Map<String, String> config;

    /**
     * description of this field
     */
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueSpec() {
        return valueSpec;
    }

    public void setValueSpec(String valueSpec) {
        this.valueSpec = valueSpec;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Field of(String name, String valueSpec) {
        Field field = new Field();
        field.setName(name);
        field.setValueSpec(valueSpec);
        return field;
    }
}
