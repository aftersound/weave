package io.aftersound.weave.batch.fs;

import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.common.NamedType;

public class FileSystemLoadControl implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("FS", FileSystemLoadControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
