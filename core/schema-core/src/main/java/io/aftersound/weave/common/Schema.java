package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.Collections;
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

    /**
     * Validation directives
     * Optional
     */
    private List<Validation> validations;

    /**
     * wrapper derived from this.fields
     */
    private transient Fields _fields;

    /**
     * create a {@link Schema} with given name and {@link Field}s
     *
     * @param name the schema name
     * @param fields the schema fields
     * @return the {@link Schema} with given name and {@link Field}s
     */
    public static Schema of(String name, List<Field> fields) {
        Schema schema = new Schema();
        schema.setName(name);
        schema.setFields(fields);
        return schema;
    }

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

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

    public Schema prepare() {
        this._fields = Fields.from(fields != null ? fields : Collections.emptyList());
        return this;
    }

    public Fields fields() {
        if (_fields == null) {
            throw new IllegalStateException("This Schema is not ready for use yet. Please 'prepare' it before using it");
        }
        return _fields;
    }
}
