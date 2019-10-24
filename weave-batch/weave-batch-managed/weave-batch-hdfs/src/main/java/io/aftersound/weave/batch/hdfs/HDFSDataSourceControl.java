package io.aftersound.weave.batch.hdfs;

import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.common.NamedType;

public class HDFSDataSourceControl extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of("HDFS", HDFSDataSourceControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
