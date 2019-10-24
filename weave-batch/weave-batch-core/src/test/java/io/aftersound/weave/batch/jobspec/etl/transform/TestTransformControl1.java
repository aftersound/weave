package io.aftersound.weave.batch.jobspec.etl.transform;

import io.aftersound.weave.common.NamedType;

public class TestTransformControl1 implements TransformControl {

    public static final NamedType<TransformControl> TYPE = NamedType.of(
            "TEST1",
            TestTransformControl1.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
