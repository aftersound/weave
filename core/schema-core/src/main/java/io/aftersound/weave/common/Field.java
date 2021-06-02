package io.aftersound.weave.common;

/**
 * A field, conceptually, in a context where it matters.
 */
public class Field {

    /**
     * name of this field
     */
    private String name;

    /**
     * type of the value of this field
     */
    private String type;

    /**
     * The specification of field value regarding how to get and parse value in
     * a record. The format o the specification is largely determined in the
     * context of component which references it.
     */
    private String valueFuncSpec;

    /**
     * Source field(s) which this field maps from
     */
    private String source;

    /**
     * description of this field
     */
    private String desc;

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

    public String getValueFuncSpec() {
        return valueFuncSpec;
    }

    public void setValueFuncSpec(String valueFuncSpec) {
        this.valueFuncSpec = valueFuncSpec;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Field of(String name, String type, String valueSpec, String sourceSpec) {
        assert (name != null && !name.isEmpty());
        assert (type != null && !type.isEmpty());
        assert (valueSpec != null && !valueSpec.isEmpty());

        Field field = new Field();
        field.setName(name);
        field.setType(type);
        field.setValueFuncSpec(valueSpec);
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
        if (valueFuncSpec != null && valueFuncSpec.length() > 0) {
            sb.append(",").append(valueFuncSpec);
        }
        if (source != null && source.length() > 0) {
            sb.append(",").append(source);
        }
        if (desc != null && desc.length() > 0) {
            sb.append(",").append(desc);
        }
        return sb.toString();
    }

}
