package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingDeriver implements Deriver {

    public static final NamedType<DeriveControl> COMPANION_CONTROL_TYPE = MappingDeriveControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public List<String> derive(DeriveControl deriveControl, ParamValueHolder sourceValueHolder) {
        MappingDeriveControl control = (MappingDeriveControl) deriveControl;
        if (control == null ||
            control.getValueMapping() == null ||
            control.getValueMapping().isEmpty() ||
            sourceValueHolder.getRawValues() == null ||
            sourceValueHolder.getRawValues().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> mappedValues = new ArrayList<>();
        for (String rawValue : sourceValueHolder.getRawValues()) {
            String mappedValue = control.getValueMapping().get(rawValue);
            if (mappedValue != null) {
                mappedValues.add(mappedValue);
            }
        }
        return mappedValues;
    }

}
