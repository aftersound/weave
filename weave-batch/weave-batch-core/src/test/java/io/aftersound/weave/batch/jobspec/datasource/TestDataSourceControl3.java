package io.aftersound.weave.batch.jobspec.datasource;

import io.aftersound.weave.common.NamedType;

public class TestDataSourceControl3 extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of(
            "TEST3",
            TestDataSourceControl3.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
