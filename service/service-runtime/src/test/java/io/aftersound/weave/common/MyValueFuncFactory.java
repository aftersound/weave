package io.aftersound.weave.common;

import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.utils.TreeNode;

public class MyValueFuncFactory extends ValueFuncFactory {

    @Override
    public <S, T> ValueFunc<S, T> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("TO_STRING".equals(funcName)) {
            return toStringFunc();
        }

        return null;
    }

    private ValueFunc toStringFunc() {
        return new ValueFunc<Object, String>() {
            @Override
            public String apply(Object source) {
                return source != null ? source.toString() : null;
            }
        };
    }

}
