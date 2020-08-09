package io.aftersound.weave.common;

import java.util.List;

public class ViewConfig {

    /**
     * name of view type
     */
    private String type;

    /**
     * schema of view
     */
    private Schema schema;

    /**
     * name of records in the view
     */
    private String recordsName;

    /**
     * names of fields automatically included in view records
     */
    private List<String> defaultOutputFields;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public String getRecordsName() {
        return recordsName;
    }

    public void setRecordsName(String recordsName) {
        this.recordsName = recordsName;
    }

    public List<String> getDefaultOutputFields() {
        return defaultOutputFields;
    }

    public void setDefaultOutputFields(List<String> defaultOutputFields) {
        this.defaultOutputFields = defaultOutputFields;
    }

}
