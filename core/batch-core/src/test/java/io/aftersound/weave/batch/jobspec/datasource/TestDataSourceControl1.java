package io.aftersound.weave.batch.jobspec.datasource;

import io.aftersound.weave.common.NamedType;

public class TestDataSourceControl1 extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of(
            "TEST1",
            TestDataSourceControl1.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
