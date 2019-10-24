package io.aftersound.weave.batch.swift;

import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.common.NamedType;

public class SwiftLoadControl implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("SWIFT", SwiftLoadControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
