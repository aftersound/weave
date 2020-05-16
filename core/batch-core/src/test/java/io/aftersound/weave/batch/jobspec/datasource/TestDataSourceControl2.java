package io.aftersound.weave.batch.jobspec.datasource;

import io.aftersound.weave.common.NamedType;

public class TestDataSourceControl2 extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of(
            "TEST2",
            TestDataSourceControl2.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
