package io.aftersound.weave.common;

import java.io.Serializable;

public class CommonSchemaHandle implements Serializable {

    private final Schema schema;
    private transient Fields fields;

    public CommonSchemaHandle(Schema schema) {
        this.schema = schema;
        this.fields = Fields.from(schema.getFields());
    }

    public Schema schema() {
        return schema;
    }

    public Fields fields() {
        if (fields == null) {
            fields = Fields.from(schema.getFields());
        }
        return fields;
    }

}
