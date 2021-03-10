package io.aftersound.weave.sample.extension.value;

import io.aftersound.weave.common.*;

public class PassThroughFuncFactory extends ValueFuncFactory {

    public static final NamedType<ValueFuncControl> COMPANION_CONTROL_TYPE = PassThroughFuncControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public <S, T> ValueFunc<S, T> createValueFunc(String valueFuncSpec) {
        ValueFuncHelper.parseAndValidate(valueFuncSpec, PassThroughFuncControl.TYPE.name());
        return (ValueFunc<S, T>) new PassThroughFunc<>();
    }

}
