package io.aftersound.weave.service.shell;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class ShellServiceExecutionControl implements ExecutionControl {

    public static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "Shell",
            ShellServiceExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

}
