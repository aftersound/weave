package io.aftersound.weave.batch.hdfs;

import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.common.NamedType;

public class HDFSLoadControl implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("HDFS", HDFSLoadControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
