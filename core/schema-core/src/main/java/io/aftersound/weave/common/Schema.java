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
     * indicate this schema is Cassandra table schema
     * or MySQL table schema, etc.
     */
    private String kind;

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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
