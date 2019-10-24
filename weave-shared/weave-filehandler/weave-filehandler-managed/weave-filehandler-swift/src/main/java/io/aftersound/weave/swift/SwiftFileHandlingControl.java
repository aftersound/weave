package io.aftersound.weave.swift;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.WithDataClientReference;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.filehandler.WithFileFilterControl;

public class SwiftFileHandlingControl implements FileHandlingControl, WithDataClientReference, WithFileFilterControl {

    static final NamedType<FileHandlingControl> TYPE = NamedType.of(
            "SWIFT",
            SwiftFileHandlingControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String clientId;
    private FileFilterControl fileFilterControl;
    private String containerName;

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

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
}
