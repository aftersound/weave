package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Func;

import java.util.ArrayList;
import java.util.List;

public class ChainFunc extends AbstractFuncWithHints<Object, Object> {

    private final List<Func<Object, Object>> chain;

    public ChainFunc(List<Func<Object, Object>> chain) {
        this.chain = chain;
    }

    @Override
    public Object apply(Object source) {
        Object v = source;
        for (Func<Object, Object> func : chain) {
            v = func.apply(v);
        }
        return v;
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Builder {

        private final List<Func<Object, Object>> chain;

        private Builder() {
            this.chain = new ArrayList<>();
        }


        public Builder with(Func func) {
            if (func != null) {
                chain.add(func);
            }
            return this;
        }

        public <IN, OUT> Func<IN, OUT> build() {
            return (Func<IN, OUT>) new ChainFunc(chain);
        }
    }

}
