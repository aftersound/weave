package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.List;

public class AliasDeriver implements Deriver {

    public static final NamedType<DeriveControl> COMPANION_CONTROL_TYPE = AliasDeriveControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public List<String> derive(DeriveControl deriveControl, ParamValueHolder sourceValueHolder) {
        return sourceValueHolder.getRawValues();
    }
}
