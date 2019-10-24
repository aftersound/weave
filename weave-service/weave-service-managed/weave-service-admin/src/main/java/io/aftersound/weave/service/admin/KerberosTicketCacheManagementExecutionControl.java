package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class KerberosTicketCacheManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "KerberosTicketCacheManagement",
            KerberosTicketCacheManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
