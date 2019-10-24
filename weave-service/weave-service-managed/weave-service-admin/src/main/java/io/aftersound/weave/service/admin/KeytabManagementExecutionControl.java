package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class KeytabManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "KeytabManagement",
            KeytabManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String securityDirectory;

    public String getSecurityDirectory() {
        return securityDirectory;
    }

    public void setSecurityDirectory(String securityDirectory) {
        this.securityDirectory = securityDirectory;
    }
}
