package io.aftersound.weave.batch.jobspec.fetl.file;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.filehandler.FileHandlingControl;

public class TestFileHandlingControl implements FileHandlingControl {

    public static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "TEST",
            TestFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
