package io.aftersound.func;


import io.aftersound.util.Registry;

import java.util.function.Function;

public class FuncRegistry extends Registry<String, Func<?,?>> {

    private final Function<String, Func<?, ?>> factory;

    public FuncRegistry(Function<String, Func<?, ?>> factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public <IN, OUT> Func<IN, OUT> getFunc(String directive) {
        return (Func<IN, OUT>) super.get(directive, factory);
    }

}
