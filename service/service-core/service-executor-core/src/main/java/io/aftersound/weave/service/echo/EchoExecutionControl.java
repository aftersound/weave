package io.aftersound.weave.service.echo;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class EchoExecutionControl implements ExecutionControl {

    public static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "EchoService",
            EchoExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
