package io.aftersound.weave.common;

import io.aftersound.weave.utils.TreeNode;

public class T1ValueFuncFactory extends ValueFuncFactory {

    @Override
    public <S, T> ValueFunc<S, T> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("T1:LOWER_CASE".equals(funcName)) {
            return (ValueFunc<S, T>) new ValueFunc<String, String>() {
                @Override
                public String apply(String source) {
                    return source != null ? source.toLowerCase() : null;
                }
            };
        }

        return null;
    }

}