package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.common.valuefunc.Example;
import io.aftersound.weave.utils.TreeNode;

import java.util.Arrays;
import java.util.Collection;

public class T1ValueFuncFactory extends ValueFuncFactory {

    private static final Collection<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("T1:LOWER_CASE", "String", "String")
                    .withAliases("T1:LC")
                    .withDescription("Transform the input String value into lowercase form")
                    .withExamples(
                            Example.as(
                                    "T1:LOWER_CASE()",
                                    "Transform the input String value into lowercase form"
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
