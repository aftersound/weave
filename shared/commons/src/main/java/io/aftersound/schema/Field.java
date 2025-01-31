package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import io.aftersound.func.Directive;
import io.aftersound.func.FuncFactory;

import java.io.Serializable;
import java.util.*;

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
        if (_directives == null) {
            throw new IllegalStateException(String.format("Field '%s' has not been initialized yet", name));
        }

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
        Type type = ProtoTypes.ARRAY.create();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder booleanFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.BOOLEAN.create());
    }

    public static Builder bytesFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.BYTES.create());
    }

    public static Builder charFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.CHAR.create());
    }

    public static Builder dateFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.DATE.create());
    }

    public static Builder doubleFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.DOUBLE.create());
    }

    public static Builder integerFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.INTEGER.create());
    }

    public static Builder floatFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.FLOAT.create());
    }

    public static Builder listFieldBuilder(String fieldName, Type elementType) {
        Type type = ProtoTypes.LIST.create();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder longFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.LONG.create());
    }

    public static Builder mapFieldBuilder(String fieldName, Type keyType, Type valueType) {
        Type type = ProtoTypes.MAP.create();
        type.setKeyType(keyType);
        type.setValueType(valueType);
        return new Builder(fieldName, type);
    }

    public static Builder objectFieldBuilder(String fieldName, Field... fields) {
        Type type = ProtoTypes.OBJECT.create();
        type.setFields(Arrays.asList(fields));
        return new Builder(fieldName, type);
    }

    public static Builder setFieldBuilder(String fieldName, Type elementType) {
        Type type = ProtoTypes.SET.create();
        type.setElementType(elementType);
        return new Builder(fieldName, type);
    }

    public static Builder shortFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.SHORT.create());
    }

    /**
     * Create a {@link Builder} which builds a {@link Field} with given name
     *
     * @param fieldName the field name
     * @return {@link Builder}
     */
    public static Builder stringFieldBuilder(String fieldName) {
        return new Builder(fieldName, ProtoTypes.STRING.create());
    }

    public static class Builder {

        private final String name;
        private final Type type;
        private String friendlyName;
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

        public Builder withFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
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
            f.setFriendlyName(friendlyName);
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
