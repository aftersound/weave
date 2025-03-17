package io.aftersound.service.discovery;

import io.aftersound.common.NamedType;
import io.aftersound.service.metadata.ExecutionControl;

public class OpenAPISpecExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "OpenAPISpecService",
            OpenAPISpecExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
