package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import io.aftersound.func.FuncFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Definition of field
 */
public class Field implements Serializable {

    /**
     * The name of this field
     */
    private String name;

    /**
     * The friendly name of this field
     */
    private String friendlyName;

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
     * Acceptable values for this field
     * Optional
     */
    private List<Object> values;

    /**
     * The function directives. It is largely determined by the context with regard to
     * how to honor the function directives.
     * Optional
     */
    private List<Directive> directives;

    /**
     * The fully qualified path
     */
    private transient String path;

    /**
     * The function directive by label
     */
    private transient Dictionary<Directive> _directives;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
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

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    public void initDirectives(FuncFactory funcFactory) {
        this._directives = Util.initDirectivesAndCreateDictionary(directives, funcFactory);

        if (type != null) {
            // TODO
        }
    }

    public Dictionary<Directive> directives() {
        return _directives;
    }

    public Constraint constraint() {
        return constraint != null ? constraint : Constraint.optional();
    }

    public boolean hasTag(String key) {
        return tags != null && tags.containsKey(key);
    }

    public boolean hasTag(String key, Object value) {
        return tags != null && tags.containsKey(key) && value.equals(tags.get(key));
    }

    public void path(String path) {
        this.path = path;
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
        private Constraint constraint;
        private String description;
        private List<Object> values;
        private List<Directive> directives;
        private Map<String, Object> tags;
        private String path;

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

        public Builder withTags(Map<String, Object> tags) {
            if (this.tags == null) {
                this.tags = new LinkedHashMap<>();
            }
            this.tags.putAll(tags);

            return this;
        }

        public Builder withTag(String tagKey, Object tagValue) {
            if (this.tags == null) {
                this.tags = new LinkedHashMap<>();
            }
            this.tags.put(tagKey, tagValue);

            return this;
        }

        public Builder withValues(List<Object> values) {
            this.values = values;
            return this;
        }

        public Builder withDirectives(List<Directive> directives) {
            this.directives = directives;
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
            f.setDirectives(directives);
            f.setValues(values);
            f.path(path);
            return f;
        }

    }

}
