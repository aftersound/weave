package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.Map;

/**
 * A field, conceptually, in a context where it matters.
 */
public class Field implements Serializable {

    /**
     * name of this field
     */
    private String name;

    /**
     * type of the value of this field
     */
    private String type;

    /**
     * indicate if the value is nullable. Optional.
     */
    private Boolean nullable;

    /**
     * The specification of field value regarding how to get and parse value in
     * a record. The format of the specification is largely determined in the
     * context of component which references it.
     */
    private String valueFunc;

    /**
     * Options. Optional.
     */
    private Map<String, String> options;

    /**
     * Source field(s) which this field maps from
     */
    private String source;

    /**
     * description of this field
     */
    private String description;

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

    public Boolean getNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    public String getValueFunc() {
        return valueFunc;
    }

    public void setValueFunc(String valueFunc) {
        this.valueFunc = valueFunc;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Field of(String name, String type, String valueFunc, String sourceSpec) {
        assert (name != null && !name.isEmpty());
        assert (type != null && !type.isEmpty());
        assert (valueFunc != null && !valueFunc.isEmpty());

        Field field = new Field();
        field.setName(name);
        field.setType(type);
        field.setValueFunc(valueFunc);
        field.setSource(sourceSpec);
        return field;
    }

    public String toExpr() {
        StringBuilder sb = new StringBuilder();
        sb.append("FIELD(");
        sb.append(name);
        if (type != null && type.length() > 0) {
            sb.append(",").append(type);
        }
        if (valueFunc != null && valueFunc.length() > 0) {
            sb.append(",").append(valueFunc);
        }
        if (source != null && source.length() > 0) {
            sb.append(",").append(source);
        }
        if (description != null && description.length() > 0) {
            sb.append(",").append(description);
        }
        return sb.toString();
    }

}
