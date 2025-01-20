package io.aftersound.weave.common.table;

import io.aftersound.common.AttributeHolder;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cell extends AttributeHolder {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Builder builder(String value) {
        return new Builder(value);
    }

    public static class Builder {

        private final String value;

        private Map<String, Object> attributes;

        private Builder(String value) {
            this.value = value;
        }

        public Builder withAttributes(Map<String, Object> attributes) {
            if (attributes != null) {
                if (this.attributes == null) {
                    this.attributes = new LinkedHashMap<>();
                }
                this.attributes.putAll(attributes);
            }

            return this;
        }

        public Builder withAttribute(String name, Object value) {
            if (this.attributes == null) {
                this.attributes = new LinkedHashMap<>();
            }
            this.attributes.put(name, value);

            return this;
        }

        public Cell build() {
            Cell c = new Cell();
            c.setValue(value);
            c.setAttributes(attributes);
            return c;
        }
    }

}
