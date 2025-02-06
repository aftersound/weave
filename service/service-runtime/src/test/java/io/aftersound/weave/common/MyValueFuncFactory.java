package io.aftersound.weave.common;

import io.aftersound.func.Descriptor;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.util.Arrays;
import java.util.List;

public class MyValueFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("TO_STRING", "Varies", "String")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        final String funcName = directive.getData();

        if ("TO_STRING".equals(funcName)) {
            return toStringFunc();
        }

        return null;
    }

    private Func toStringFunc() {
        return new Func<Object, String>() {
            @Override
            public String apply(Object source) {
                return source != null ? source.toString() : null;
            }
        };
    }

}
