package io.aftersound.weave.service.request;

import io.aftersound.weave.common.*;
import io.aftersound.weave.utils.TreeNode;

public class ParamReadFuncFactory extends ValueFuncFactory {

    public static final NamedType<ValueFuncControl> COMPANION_CONTROL_TYPE = ParamReadFuncControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public <S, E> ValueFunc<S, E> createValueFunc(String valueFuncSpec) {
        TreeNode treeNode = ValueFuncHelper.parseAndValidate(valueFuncSpec, COMPANION_CONTROL_TYPE.name());
        return (ValueFunc<S, E>) new ParamReadFunc(treeNode.getDataOfChildren(0));
    }
}
