package io.aftersound.weave.common;

import java.util.List;

/**
 * A {@link ValueFunc} has chain-of-responsibility behavior
 * @param <S> source/input type
 * @param <T> target/output type
 */
public class ChainOfResponsibilityValueFunc<S, T> extends ValueFunc<S, T> {

    private final List<ValueFunc<S, T>> subordinates;

    public ChainOfResponsibilityValueFunc(List<ValueFunc<S, T>> subordinates) {
        this.subordinates = subordinates;
    }

    @Override
    public T apply(S source) {
        T v = null;
        for (ValueFunc<S, T> subordinate : subordinates) {
            v = subordinate.apply(source);
            if (v != null) {
                break;
            }
        }
        return v;
    }

}
