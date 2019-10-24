package io.aftersound.weave.batch.swift;

import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.common.NamedType;

public class SwiftDataSourceControl extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of("SWIFT", SwiftDataSourceControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
