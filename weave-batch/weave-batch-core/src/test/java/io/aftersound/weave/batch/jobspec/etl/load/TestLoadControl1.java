package io.aftersound.weave.batch.jobspec.etl.load;

import io.aftersound.weave.common.NamedType;

public class TestLoadControl1 implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("TEST1", TestLoadControl1.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
