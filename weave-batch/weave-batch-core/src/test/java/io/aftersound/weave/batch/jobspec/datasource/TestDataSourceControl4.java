package io.aftersound.weave.batch.jobspec.datasource;

import io.aftersound.weave.common.NamedType;

public class TestDataSourceControl4 extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of(
            "TEST4",
            TestDataSourceControl4.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
