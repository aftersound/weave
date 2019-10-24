package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.Map;

public class MappingDeriveControl extends DeriveControl {

    static final NamedType<DeriveControl> TYPE = NamedType.of(
            "Mapping",
            MappingDeriveControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private Map<String, String> valueMapping;

    public Map<String, String> getValueMapping() {
        return valueMapping;
    }

    public void setValueMapping(Map<String, String> valueMapping) {
        this.valueMapping = valueMapping;
    }
}
