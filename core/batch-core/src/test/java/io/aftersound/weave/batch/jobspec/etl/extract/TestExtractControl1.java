package io.aftersound.weave.batch.jobspec.etl.extract;

import io.aftersound.weave.common.NamedType;

public class TestExtractControl1 implements ExtractControl {

    public static final NamedType<ExtractControl> TYPE = NamedType.of(
            "TEST1",
            TestExtractControl1.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
