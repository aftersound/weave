package io.aftersound.service.request;

import io.aftersound.schema.Type;

public class ValueMetadata {

    private final String scope;
    private final Type valueType;
    private final boolean multiValued;

    private ValueMetadata(String scope, Type valueType, boolean multiValued) {
        this.scope = scope;
        this.valueType = valueType;
        this.multiValued = multiValued;
    }

    public static ValueMetadata multiValued(String scope, Type valueType) {
        return new ValueMetadata(scope, valueType, true);
    }

    public static ValueMetadata singleValued(String scope, Type valueType) {
        return new ValueMetadata(scope, valueType, false);
    }

    public String getScope() {
        return scope;
    }

    public Type getValueType() {
        return valueType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

}
