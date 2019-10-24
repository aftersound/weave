package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class BatchAppConfigManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "BatchAppConfigManagement",
            BatchAppConfigManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String appConfigDirectory;

    public String getAppConfigDirectory() {
        return appConfigDirectory;
    }

    public void setAppConfigDirectory(String appConfigDirectory) {
        this.appConfigDirectory = appConfigDirectory;
    }

}
