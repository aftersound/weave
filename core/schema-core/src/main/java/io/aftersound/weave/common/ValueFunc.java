package io.aftersound.weave.common;

import io.aftersound.weave.utils.Handle;

/**
 * A conceptual function which acts on input source and produces output
 *
 * @param <S> generic type of source/input value
 * @param <T> generic type of target/output value
 */
public interface ValueFunc<S,T> {

    /**
     * Default instance of {@link ValueFuncRegistry}, which manages
     * {@link ValueFunc}s in according to value function specs.
     * Application can still create instances of {@link ValueFuncRegistry}
     * if DefaultInstance cannot fulfill the need.
     */
    Handle<ValueFuncRegistry> REGISTRY = Handle.of(
            "DefaultInstance",
            ValueFuncRegistry.class
    ).setAndLock(new ValueFuncRegistry());

    T process(S source);
}
