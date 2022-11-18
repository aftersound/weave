package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestValueFuncFactory extends ValueFuncFactory {

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return ValueFuncDescriptorHelper.getDescriptors(TestValueFuncFactory.class);
    }

    @Override
    public <S, T> ValueFunc<S, T> create(TreeNode spec) {

        final String funcName = spec.getData();

        if ("SCOPED".equals(funcName)) {
            return createScopedValueFunc(spec);
        }

        if ("MAP:GET".equals(funcName)) {
            return createMapGetFunc(spec);
        }

        if ("MAP:TO_STRING".equals(funcName)) {
            return createMapToStringFunc(spec);
        }

        return null;
    }

    private ValueFunc createScopedValueFunc(TreeNode spec) {
        final String scope = spec.getDataOfChildAt(0);
        final ValueFunc valueFunc = MasterValueFuncFactory.create(spec.getChildAt(1));
        if ("Record".equalsIgnoreCase(scope)) {
            return new RecordScopeValueFunc(valueFunc);
        } else {
            throw new ValueFuncException(spec + " is not supported");
        }
    }

    private ValueFunc createMapGetFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        return (ValueFunc<Map<String, Object>, Object>) source -> source != null ? source.get(key) : null;
    }

    private ValueFunc createMapToStringFunc(TreeNode spec) {
        final List<String> strList = spec.getDataOfChildren();
        return (ValueFunc<Map<String, Object>, String>) source -> {
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
        };
    }

}
