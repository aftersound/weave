package io.aftersound.weave.common;

import java.io.Serializable;

/**
 * A conceptual function which acts on input source and produces output
 *
 * @param <S> generic type of source/input value
 * @param <T> generic type of target/output value
 */
public interface ValueFunc<S,T> extends Serializable {
    T process(S source);
}
