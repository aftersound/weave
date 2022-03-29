package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.List;

public class Schema implements Serializable {

    /**
     * The name of this schema
     */
    private String name;

    /**
     * Optional type hint of this schema, which could
     * indicate this schema is Cassandra table schema.
     */
    private String type;

    /**
     * The list of fields in this schema
     */
    private List<Field> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
