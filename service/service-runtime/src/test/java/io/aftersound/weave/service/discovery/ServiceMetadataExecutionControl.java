package io.aftersound.weave.service.discovery;

import io.aftersound.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class ServiceMetadataExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "ServiceMetadataService",
            ServiceMetadataExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
