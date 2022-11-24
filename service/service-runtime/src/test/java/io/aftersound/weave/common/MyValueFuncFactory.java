package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.Arrays;
import java.util.Collection;

public class MyValueFuncFactory extends ValueFuncFactory {

    private static final Collection<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("TO_STRING", "Varies", "String")
                    .build()
    );

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return DESCRIPTORS;
    }

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
