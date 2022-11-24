package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.common.valuefunc.Example;
import io.aftersound.weave.utils.TreeNode;

import java.util.Arrays;
import java.util.Collection;

public class T2ValueFuncFactory extends ValueFuncFactory {

    private static final Collection<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("T2:UPPER_CASE", "String", "String")
                    .withAliases("T2:UC")
                    .withDescription("Transform the input String value into UPPERCASE form")
                    .withExamples(
                            Example.as(
                                    "T2:UPPER_CASE()",
                                    "Transform the input String value into UPPERCASE form"
                            )
                    )
                    .build()
    );

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return DESCRIPTORS;
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
