package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of field
 */
public class Field implements Serializable {

    private static final String PRIMARY = "primary";
    private static final String NOT_NULLABLE = "notNullable";

    private String name;


    private Type type;

    /**
     * Optional.
     * The func spec serves as directive w.r.t how to get and parse value from source.
     * How to honor the func spec is largely determined by the context that uses this
     * field.
     */
    private String func;

    /**
     * The constraint of this field, indicating if it's required, optional, etc.
     * When missing, it's regarded as optional.
     */
    private Constraint constraint;


    private String description;
    private List<String> values;
    private List<Validation> validations;
    private Map<String, Object> hints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

    public Map<String, Object> getHints() {
        return hints;
    }

    public void setHints(Map<String, Object> hints) {
        this.hints = hints;
    }

    public boolean primary() {
        return hints != null && Boolean.TRUE.equals(hints.get(PRIMARY));
    }

    public boolean notNullable() {
        return hints != null && Boolean.TRUE.equals(hints.get(NOT_NULLABLE));
    }

    public static Builder builder(String name, Type type) {
        return new Builder(name, type);
    }

    public static Builder arrayFieldBuilder(String fieldName, Type elementType) {
        Type type = TypeEnum.ARRAY.createType();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder booleanFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.BOOLEAN.createType());
    }

    public static Builder bytesFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.BYTES.createType());
    }

    public static Builder charFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.CHAR.createType());
    }

    public static Builder dateFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.DATE.createType());
    }

    public static Builder doubleFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.DOUBLE.createType());
    }

    public static Builder intFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.INT.createType());
    }

    public static Builder floatFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.FLOAT.createType());
    }

    public static Builder listFieldBuilder(String fieldName, Type elementType) {
        Type type = TypeEnum.LIST.createType();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder longFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.LONG.createType());
    }

    public static Builder mapFieldBuilder(String fieldName, Type keyType, Type valueType) {
        Type type = TypeEnum.MAP.createType();
        type.setKeyType(keyType);
        type.setValueType(valueType);
        return new Builder(fieldName, type);
    }

    public static Builder objectFieldBuilder(String fieldName, Field... fields) {
        Type type = TypeEnum.OBJECT.createType();
        type.setFields(Arrays.asList(fields));
        return new Builder(fieldName, type);
    }

    public static Builder setFieldBuilder(String fieldName, Type elementType) {
        Type type = TypeEnum.SET.createType();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder shortFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.SHORT.createType());
    }

    public static Builder stringFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.STRING.createType());
    }

    public static class Builder {

        private final String name;
        private final Type type;
        private String func;
        private Constraint constraint;
        private String description;
        private List<String> values;
        private List<Validation> validations;
        private Map<String, Object> hints;

        private Builder(String fieldName, Type type) {
            this.name = fieldName;
            this.type = type;
        }

        public Builder withFunc(String func) {
            this.func = func;
            return this;
        }

        public Builder primary() {
            if (hints == null) {
                hints = new LinkedHashMap<>();
            }
            this.hints.put(PRIMARY, Boolean.TRUE);
            return this;
        }

        public Builder notNullable() {
            if (hints == null) {
                hints = new LinkedHashMap<>();
            }
            this.hints.put(NOT_NULLABLE, Boolean.TRUE);
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withValues(List<String> values) {
            this.values = values;
            return this;
        }
        
        public Builder withValidations(List<Validation> validations) {
            this.validations = validations;
            return this;
        }

        public Field build() {
            Field f = new Field();
            f.setName(name);
            f.setType(type);
            f.setFunc(func);
            f.setConstraint(constraint);
            f.setDescription(description);
            f.setValues(values);
            f.setValidations(validations);
            f.setHints(hints);
            return f;
        }

    }

}
