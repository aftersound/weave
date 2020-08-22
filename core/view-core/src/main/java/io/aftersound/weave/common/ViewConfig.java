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
     * elements included in the view output
     */
    private List<Element> elements;

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

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
