package io.aftersound.schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Type of field value
 */
public class Type implements Serializable {

    /**
     * the name of this type
     */
    private String name;

    /**
     * options for this type, optional
     */
    private Map<String, Object> options;

    /**
     * applicable only when this type is OBJECT
     */
    private List<Field> fields;

    /**
     * applicable only when this type is ARRAY, LIST, or SET
     */
    private Type elementType;

    /**
     * applicable only when this type is MAP
     */
    private Type keyType;

    /**
     * applicable only when this type is MAP
     */
    private Type valueType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Type getElementType() {
        return elementType;
    }

    public void setElementType(Type elementType) {
        this.elementType = elementType;
    }

    public Type getKeyType() {
        return keyType;
    }

    public void setKeyType(Type keyType) {
        this.keyType = keyType;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;
        private Map<String, Object> options;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withOptions(Map<String, Object> options) {
            this.options = options;
            return this;
        }

        public Type build() {
            Type type = new Type();
            type.setName(name);
            type.setOptions(options);
            return type;
        }

    }

}
