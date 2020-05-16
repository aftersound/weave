package io.aftersound.weave.batch.jobspec.ft.file;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.filehandler.FileHandlingControl;

public class TargetFileHandlingControl implements FileHandlingControl {

    public static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "TARGET_TYPE",
            TargetFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
