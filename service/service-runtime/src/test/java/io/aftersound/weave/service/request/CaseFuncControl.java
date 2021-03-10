package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.ValueFuncControl;

public class CaseFuncControl implements ValueFuncControl {

    public static final NamedType<ValueFuncControl> TYPE = NamedType.of(
            "CASE",
            CaseFuncControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String caseType;

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    @Override
    public String asValueFuncSpec() {
        return TYPE.name() + "(" + caseType + ")";
    }

}
