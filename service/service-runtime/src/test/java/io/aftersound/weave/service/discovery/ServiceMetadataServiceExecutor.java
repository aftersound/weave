package io.aftersound.weave.service.discovery;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.Util;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.utils.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceMetadataServiceExecutor extends ServiceExecutor<Map<String, List<ServiceMetadata>>> {

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
    public Map<String, List<ServiceMetadata>> execute(
            ExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        ServiceMetadataExecutionControl ec = Util.safeCast(executionControl, ServiceMetadataExecutionControl.class);
        if (!validate(ec, context)) {
            return null;
        }

        return list(ec, request, context);
    }

    private boolean validate(ServiceMetadataExecutionControl ec, ServiceContext context) {
        if (ec == null) {
            LOGGER.error("ExecutionControl is not instance of {}", ServiceMetadataExecutionControl.class.getName());
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ServiceMetadataExecutionControl is null"));
            return false;
        }

        if (ec.getServiceMetadataRegistries() == null || ec.getServiceMetadataRegistries().isEmpty()) {
            LOGGER.error("ExecutionControl.serviceMetadataRegistries is missing or empty");
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ServiceMetadataExecutionControl is malformed"));
            return false;
        }

        return true;
    }

    private Map<String, List<ServiceMetadata>> list(
            ServiceMetadataExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        List<String> targetPaths = request.firstWithName("path").multiValues(String.class);
        List<String> targetTypes = request.firstWithName("type").multiValues(String.class);

        List<ServiceMetadata> serviceMetadataList = new ArrayList<>();

        for (String serviceMetadataRegistryName : ec.getServiceMetadataRegistries()) {
            ServiceMetadataRegistry serviceMetadataRegistry = componentRepository.getComponent(
                    serviceMetadataRegistryName,
                    ServiceMetadataRegistry.class
            );

            if (serviceMetadataRegistry != null) {
                for (ServiceMetadata metadata : serviceMetadataRegistry.all()) {
                    if ((targetPaths.isEmpty() || targetPaths.contains(metadata.getPath())) &&
                            (targetTypes.isEmpty() || targetTypes.contains(metadata.getExecutionControl().getType()))) {
                        serviceMetadataList.add(metadata);
                    }
                }
            }
        }

        return MapBuilder.hashMap().kv("services", serviceMetadataList).build();
    }

}
