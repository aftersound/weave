package io.aftersound.weave.sample.extension.value;

import io.aftersound.weave.common.ValueFunc;

public class PassThroughFunc<T> implements ValueFunc<T,T> {

    @Override
    public T process(T source) {
        return source;
    }

}
