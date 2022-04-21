package io.aftersound.weave.service.discovery;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.request.ParamValueHolders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAPISpecServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = OpenAPISpecExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPISpecServiceExecutor.class);

    public OpenAPISpecServiceExecutor(ComponentRepository componentRepository) {
        super(componentRepository);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ExecutionControl control, ParamValueHolders request, ServiceContext context) {
        return null;
    }

}
