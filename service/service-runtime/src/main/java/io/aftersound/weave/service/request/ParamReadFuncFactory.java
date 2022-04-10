package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TreeNode;

import java.util.Arrays;
import java.util.List;

public class ParamReadFuncFactory extends ValueFuncFactory {

    @Override
    public <S, E> ValueFunc<S, E> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("PARAM:READ".equals(funcName)) {
            return (ValueFunc<S, E>) new ParamReadFunc(spec.getDataOfChildren());
        }

        return null;
    }

}
