package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class JobSpecManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "JobSpecManagement",
            JobSpecManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String metadataDirectory;

    public String getMetadataDirectory() {
        return metadataDirectory;
    }

    public void setMetadataDirectory(String metadataDirectory) {
        this.metadataDirectory = metadataDirectory;
    }

}
