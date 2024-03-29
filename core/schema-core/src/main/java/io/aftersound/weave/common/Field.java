package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.*;

/**
 * Definition of field
 */
public class Field implements Serializable {

    private static final String PRIMARY = "primary";
    private static final String NOT_NULLABLE = "notNullable";

    /**
     * The name of this field
     */
    private String name;

    /**
     * The value type of this field
     */
    private Type type;

    /**
     * The constraint of this field, indicating if it's required, optional, etc.
     * When missing, the value is regarded as optional.
     */
    private Constraint constraint;

    /**
     * The description of this field.
     * Optional.
     */
    private String description;

    /**
     * Additional tags, such as indicating if this field is primary, not nullable, etc.
     * Optional
     */
    private Map<String, Object> tags;

    /**
     * The func spec serves as directive w.r.t how to get and parse value from source.
     * How to honor the func spec is largely determined by the context that uses this
     * field.
     * Optional
     */
    private String func;

    /**
     * Acceptable values for this field
     * Optional
     */
    private List<Object> values;

    /**
     * Validation directives
     * Optional
     */
    private List<Validation> validations;

    /**
     * The fully qualified path
     */
    private transient String path;

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

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Validation> getValidations() {
        return validations;
    }

    public void setValidations(List<Validation> validations) {
        this.validations = validations;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Constraint constraint() {
        return constraint != null ? constraint : Constraint.optional();
    }

    public boolean primary() {
        return tags != null && Boolean.TRUE.equals(tags.get(PRIMARY));
    }

    public boolean notNullable() {
        return tags != null && Boolean.TRUE.equals(tags.get(NOT_NULLABLE));
    }

    public String path() {
        return path != null ? path : name;
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

    public static Builder integerFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.INTEGER.createType());
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

    /**
     * Create a {@link Builder} which builds a {@link Field} with given name
     *
     * @param fieldName the field name
     * @return {@link Builder}
     */
    public static Builder stringFieldBuilder(String fieldName) {
        return new Builder(fieldName, TypeEnum.STRING.createType());
    }

    public static class Builder {

        private final String name;
        private final Type type;
        private String func;
        private Constraint constraint;
        private String description;
        private List<Object> values;
        private List<Validation> validations;
        private String path;
        private Map<String, Object> tags;

        private Builder(String fieldName, Type type) {
            this.name = fieldName;
            this.type = type;
        }

        public Builder withTypeOptions(Map<String, Object> typeOptions) {
            if (this.type.getOptions() == null) {
                this.type.setOptions(new LinkedHashMap<>());
            }
            this.type.getOptions().putAll(typeOptions);
            return this;
        }

        public <V> Builder withTypeOption(String optionKey, V optionValue) {
            if (this.type.getOptions() == null) {
                this.type.setOptions(new LinkedHashMap<>());
            }
            this.type.getOptions().put(optionKey, optionValue);
            return this;
        }

        public Builder withConstraint(Constraint constraint) {
            this.constraint = constraint;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder primary() {
            if (tags == null) {
                tags = new LinkedHashMap<>();
            }
            this.tags.put(PRIMARY, Boolean.TRUE);
            return this;
        }

        public Builder notNullable() {
            if (tags == null) {
                tags = new LinkedHashMap<>();
            }
            this.tags.put(NOT_NULLABLE, Boolean.TRUE);
            return this;
        }

        public Builder withFunc(String func) {
            this.func = func;
            return this;
        }

        public Builder withValues(List<Object> values) {
            this.values = values;
            return this;
        }
        
        public Builder withValidations(List<Validation> validations) {
            this.validations = validations;
            return this;
        }

        public Builder withPath(String path) {
            this.path = path;
            return this;
        }

        public Field build() {
            Field f = new Field();
            f.setName(name);
            f.setType(type);
            f.setConstraint(constraint);
            f.setDescription(description);
            f.setTags(tags);
            f.setFunc(func);
            f.setValues(values);
            f.setValidations(validations);
            f.setPath(path);
            return f;
        }

    }

}
