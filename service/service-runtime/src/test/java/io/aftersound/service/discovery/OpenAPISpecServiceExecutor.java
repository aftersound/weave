package io.aftersound.service.discovery;

import io.aftersound.common.NamedType;
import io.aftersound.component.ComponentRepository;
import io.aftersound.service.ServiceContext;
import io.aftersound.service.ServiceExecutor;
import io.aftersound.service.metadata.ExecutionControl;
import io.aftersound.service.request.ParamValueHolders;
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
