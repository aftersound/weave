package io.aftersound.weave.batch.knox;

import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.common.NamedType;

public class KnoxLoadControl implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("KNOX", KnoxLoadControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
