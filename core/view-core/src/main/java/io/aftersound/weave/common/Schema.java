package io.aftersound.weave.common;

import java.util.List;

/**
 * Conceptual schema consists of a list of {@link Field}s
 */
public class Schema {

    /**
     * name of the schema, how to use/interpret the name depends on the context
     */
    private String name;

    /**
     * a list of {@link Field}s in the schema
     */
    private List<Field> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

}
