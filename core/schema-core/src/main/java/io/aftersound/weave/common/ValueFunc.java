package io.aftersound.weave.common;

/**
 * A conceptual function which acts on input source and produces output
 *
 * @param <S> generic type of source/input value
 * @param <T> generic type of target/output value
 */
public interface ValueFunc<S,T> {
    T process(S source);
}
