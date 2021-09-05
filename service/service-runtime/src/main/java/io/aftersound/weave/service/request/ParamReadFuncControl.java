package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.ValueFuncControl;

import java.util.List;

public class ParamReadFuncControl implements ValueFuncControl {

    public static final NamedType<ValueFuncControl> TYPE = NamedType.of(
            "PARAM:READ",
            ParamReadFuncControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private List<String> parameters;

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String asValueFuncSpec() {
        if (parameters != null && parameters.size() > 0) {
            return TYPE.name() + "(" + String.join(",", parameters) + ")";
        } else {
            return TYPE.name() + "()";
        }
    }

}
