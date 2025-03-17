package io.aftersound.service.discovery;

import io.aftersound.common.NamedType;
import io.aftersound.component.ComponentRepository;
import io.aftersound.service.ServiceContext;
import io.aftersound.service.ServiceExecutor;
import io.aftersound.service.ServiceMetadataRegistry;
import io.aftersound.service.SpecExtractor;
import io.aftersound.service.metadata.ExecutionControl;
import io.aftersound.service.metadata.Util;
import io.aftersound.service.request.ParamValueHolders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.aftersound.service.message.MessageRegistry.INTERNAL_SERVICE_ERROR;

public class ServiceMetadataServiceExecutor extends ServiceExecutor<Object> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = ServiceMetadataExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataServiceExecutor.class);

    public ServiceMetadataServiceExecutor(ComponentRepository componentRepository) {
        super(componentRepository);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(
            ExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        ServiceMetadataExecutionControl control = Util.safeCast(executionControl, ServiceMetadataExecutionControl.class);
        if (control == null) {
            context.getMessages().addMessage(
                    INTERNAL_SERVICE_ERROR.error("ExecutionControl is missing or malformed")
            );
            return null;
        }

        ServiceMetadataRegistry serviceMetadataRegistry = componentRepository.getComponent(
                ServiceMetadataRegistry.class.getSimpleName(),
                ServiceMetadataRegistry.class
        );
        if (serviceMetadataRegistry == null) {
            context.getMessages().addMessage(
                    INTERNAL_SERVICE_ERROR.error(
                            String.format(
                                    "Runtime dependency '%s' of type '%s' is not satisfied",
                                    ServiceMetadataRegistry.class.getSimpleName(),
                                    ServiceMetadataRegistry.class.getName()
                            )
                    )
            );
            return null;
        }

        return serviceMetadataRegistry.getSpec(
                new SpecExtractor<Map<String, Object>>() {

                    @Override
                    public Map<String, Object> extract(Object config) {
                        return new LinkedHashMap<>();
                    }

                }
        );
    }

}
