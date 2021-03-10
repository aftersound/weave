package io.aftersound.weave.sample.extension.value;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.ValueFuncControl;

public class PassThroughFuncControl implements ValueFuncControl {

    static final NamedType<ValueFuncControl> TYPE = NamedType.of(
            "_",
            PassThroughFuncControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    @Override
    public String asValueFuncSpec() {
        return TYPE.name() + "()";
    }

}
