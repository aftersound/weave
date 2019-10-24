package io.aftersound.weave.service.demo;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class DemoServiceExecutionControl implements ExecutionControl {

    public static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "Demo",
            DemoServiceExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }
}
