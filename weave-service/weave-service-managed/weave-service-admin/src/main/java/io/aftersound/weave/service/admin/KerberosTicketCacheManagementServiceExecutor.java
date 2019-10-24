package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.service.resources.ResourceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: figure how to manage ticket cache before detailed implementation
public class KerberosTicketCacheManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = KerberosTicketCacheManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionManagementServiceExecutor.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    public KerberosTicketCacheManagementServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        KerberosTicketCacheManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String operation = request.firstWithName("operation").singleValue(String.class);

        if ("create".equals(operation)) {
            return create(executionControl, request, context);
        }

        if ("list".equals(operation)) {
            return list(executionControl, request, context);
        }

        if ("update".equals(operation)) {
            return update(executionControl, request, context);
        }

        if ("delete".equals(operation)) {
            return delete(executionControl, request, context);
        }

        if ("start".equals(operation)) {
            return start(executionControl, request, context);
        }

        if ("stop".equals(operation)) {
            return stop(executionControl, request, context);
        }

        return list(executionControl, request, context);
    }

    private Object create(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }

    private Object list(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }

    private Object update(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }

    private Object delete(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }

    private Object start(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }

    private Object stop(
            KerberosTicketCacheManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        return null;
    }
}
