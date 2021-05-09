package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.AbstractValidation;
import io.aftersound.weave.service.metadata.param.Validation;

import java.util.Set;

public class AllowedValuesValidation extends AbstractValidation {

    static final NamedType<Validation> TYPE = NamedType.of(
            "AllowedValues",
            AllowedValuesValidation.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private Set<String> valueSet;

    public Set<String> getValueSet() {
        return valueSet;
    }

    public void setValueSet(Set<String> valueSet) {
        this.valueSet = valueSet;
    }
}
