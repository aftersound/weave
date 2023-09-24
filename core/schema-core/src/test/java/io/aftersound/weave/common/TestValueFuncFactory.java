package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.*;

public class TestValueFuncFactory extends ValueFuncFactory {

    private static final Collection<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("MAP:GET", "Map", "Varies")
                    .build(),
            Descriptor.builder("MAP:TO_STRING", "Map", "String")
                    .build()
    );

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <S, T> ValueFunc<S, T> create(TreeNode spec) {

        final String funcName = spec.getData();

        if ("MAP:GET".equals(funcName)) {
            return createMapGetFunc(spec);
        }

        if ("MAP:TO_STRING".equals(funcName)) {
            return createMapToStringFunc(spec);
        }

        return null;
    }

    private ValueFunc createMapGetFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        return new ValueFunc<Map<String, Object>, Object>() {
            @Override
            public Object apply(Map<String, Object> source) {
                return source != null ? source.get(key) : null;
            }
        };
    }

    private ValueFunc createMapToStringFunc(TreeNode spec) {
        final List<String> strList = spec.getDataOfChildren();
        return new ValueFunc<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> source) {
                if (source != null) {
                    String format = strList.get(0);
                    List<Object> values = new ArrayList<>(strList.size() - 1);
                    for (int i = 1; i < strList.size(); i++) {
                        values.add(source.get(strList.get(i)));
                    }
                    Object[] args = values.toArray(new Object[values.size()]);
                    return String.format(format, args);
                } else {
                    return null;
                }
            }
        };
    }

}
