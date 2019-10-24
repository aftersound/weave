package io.aftersound.weave.metadata;

public class ValueMetadata {

    private String scope;
    private String valueType;
    private boolean multiValued;

    private ValueMetadata(String scope, String valueType, boolean multiValued) {
        this.scope = scope;
        this.valueType = valueType;
        this.multiValued = multiValued;
    }

    public static ValueMetadata multiValued(String scope, String valueType) {
        return new ValueMetadata(scope, valueType, true);
    }

    public static ValueMetadata singleValued(String scope, String valueType) {
        return new ValueMetadata(scope, valueType, false);
    }

    public String getScope() {
        return scope;
    }

    public String getValueType() {
        return valueType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

}
