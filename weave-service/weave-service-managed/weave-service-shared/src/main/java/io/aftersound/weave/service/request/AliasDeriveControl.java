package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.param.DeriveControl;

public class AliasDeriveControl extends DeriveControl {

    static final NamedType<DeriveControl> TYPE = NamedType.of(
            "Alias",
            AliasDeriveControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
