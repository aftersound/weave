package io.aftersound.weave.common;

public final class RecordScopeValueFunc<S, T> implements ValueFunc<S, T>, RecordScope {

    private final ValueFunc<S, T> valueFunc;

    public RecordScopeValueFunc(ValueFunc<S,T> valueFunc) {
        this.valueFunc = valueFunc;
    }

    @Override
    public T apply(S source) {
        return valueFunc.apply(source);
    }

}
