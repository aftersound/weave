package io.aftersound.weave.service.request;

import io.aftersound.weave.common.Type;

public class ValueMetadata {

    private String scope;
    private Type valueType;
    private boolean multiValued;

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
