package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.Collection;

public class T2ValueFuncFactory extends ValueFuncFactory {

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return ValueFuncDescriptorHelper.getDescriptors(T2ValueFuncFactory.class);
    }

    @Override
    public <S, T> ValueFunc<S, T> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("T2:UPPER_CASE".equals(funcName)) {
            return (ValueFunc<S, T>) new ValueFunc<String, String>() {
                @Override
                public String apply(String source) {
                    return source != null ? source.toUpperCase() : null;
                }
            };
        }

        return null;
    }

}
