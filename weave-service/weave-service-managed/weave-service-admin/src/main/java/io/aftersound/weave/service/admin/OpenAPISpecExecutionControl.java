package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class OpenAPISpecExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "OpenAPISpec",
            OpenAPISpecExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
