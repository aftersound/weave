package io.aftersound.weave.hdfs;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.WithDataClientReference;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.filehandler.WithFileFilterControl;

public class HDFSFileHandlingControl implements FileHandlingControl, WithDataClientReference, WithFileFilterControl {

    static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "HDFS",
            HDFSFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String clientId;
    private FileFilterControl fileFilterControl;
    private String directory;

    @Override
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

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
