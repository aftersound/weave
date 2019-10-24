package io.aftersound.weave.fs;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.filehandler.WithFileFilterControl;

public class FSFileHandlingControl implements FileHandlingControl, WithFileFilterControl {

    static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "FS",
            FSFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private FileFilterControl fileFilterControl;
    private String directory;

    @Override
    public FileFilterControl getFileFilterControl() {
        return fileFilterControl;
    }

    public void setFileFilterControl(FileFilterControl fileFilterControl) {
        this.fileFilterControl = fileFilterControl;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

}
