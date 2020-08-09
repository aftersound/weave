package io.aftersound.weave.common;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Conceptual field in a row/record
 */
public class Field {

    /**
     * name of this field
     */
    private String name;

    /**
     * specification of field value
     */
    private String valueSpec;

    /**
     * names of source fields, from which this field is derived. Optional
     */
    private List<String> sourceFieldNames;

    /**
     * multi-purpose configuration, such as those for parsing field value from source fields
     */
    private Map<String, String> config;

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

    public List<String> getSourceFieldNames() {
        return sourceFieldNames;
    }

    public void setSourceFieldNames(List<String> sourceFieldNames) {
        this.sourceFieldNames = sourceFieldNames;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public static Field of(String name, List<String> sourceFieldNames, Map<String, String> config) {
        Field field = new Field();
        field.setName(name);
        field.setSourceFieldNames(sourceFieldNames);
        field.setConfig(config);
        return field;
    }

    public static Field of(String name, List<String> sourceFieldNames) {
        Field field = new Field();
        field.setName(name);
        field.setSourceFieldNames(sourceFieldNames);
        return field;
    }

    public static Field of(String name, String sourceFieldName) {
        Field field = new Field();
        field.setName(name);
        if (sourceFieldName != null) {
            field.setSourceFieldNames(Arrays.asList(sourceFieldName));
        }
        return field;
    }

}
