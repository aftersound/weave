package io.aftersound.weave.service.echo;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ManagedComponents;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.request.ParamValueHolders;

import java.util.Map;

/**
 * This {@link ServiceExecutor} echos parameter values in request as response.
 * This is an example of implementation of {@link ServiceExecutor}, also could
 * serve as a tool to verify service interface design.
 */
public class EchoServiceExecutor extends ServiceExecutor<Map<String, Object>> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = EchoExecutionControl.TYPE;

    public EchoServiceExecutor(ManagedComponents managedComponents) {
        super(managedComponents);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Map<String, Object> execute(ExecutionControl control, ParamValueHolders request, ServiceContext context) {
        return request.asUnmodifiableMap();
    }

}
