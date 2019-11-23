package io.aftersound.weave.metadata;

public class Field {

    private String name;

    private String description;

    private String valueType;

    private boolean multiValued;
    // Only when this is multi-valued field
    private String valueDelimiter;

    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getValueDelimiter() {
        return valueDelimiter;
    }

    public void setValueDelimiter(String valueDelimiter) {
        this.valueDelimiter = valueDelimiter;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}
