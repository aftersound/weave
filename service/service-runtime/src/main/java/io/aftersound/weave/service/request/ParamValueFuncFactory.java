package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncDescriptorHelper;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.Collection;

public class ParamValueFuncFactory extends ValueFuncFactory {

    @Override
    public Collection<Descriptor> getValueFuncDescriptors() {
        return ValueFuncDescriptorHelper.getDescriptors(ParamValueFuncFactory.class);
    }

    @Override
    public <S, E> ValueFunc<S, E> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("PARAM:READ".equals(funcName)) {
            return (ValueFunc<S, E>) new ParamReadFunc(spec.getDataOfChildren());
        }

        return null;
    }

}
