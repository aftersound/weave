package io.aftersound.weave.service.management;

import com.fasterxml.jackson.databind.JsonNode;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.Util;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.utils.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.aftersound.weave.service.message.MessageRegistry.BAD_REQUEST;
import static io.aftersound.weave.service.message.MessageRegistry.CONFLICT;

public class InstanceManagementServiceExecutor extends ServiceExecutor<Object> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = InstanceManagementExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceManagementServiceExecutor.class);

    public InstanceManagementServiceExecutor(ComponentRepository componentRepository) {
        super(componentRepository);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        if (!validate(executionControl, context)) {
            return Collections.emptyMap();
        }

        InstanceManagementExecutionControl ec = (InstanceManagementExecutionControl) executionControl;

        String opName = request.firstWithName("operation").singleValue(String.class);
        Operation op = Operation.byScopeAndName(Scope.Instance, opName);
        if (op == null) {
            context.getMessages().addMessage(MessageRegistry.INVALID_PARAMETER_VALUE.error("operation", "String", opName));
            return null;
        }
        switch (op) {
            case RegisterInstance:
                return registerInstance(ec, request, context);
            case UnregisterInstance:
                return unregisterInstance(ec, request, context);
            case MarkDownInstance:
                return markDownInstance(ec, request, context);
            case MarkUpInstance:
                return markUpInstance(ec, request, context);
            case FindInstances:
                return findInstances(ec, request, context);
            default: {
                context.getMessages().addMessage(BAD_REQUEST.error(String.format("Operation '%s' is not supported", opName)));
                return null;
            }
        }
    }

    private boolean validate(ExecutionControl executionControl, ServiceContext context) {
        InstanceManagementExecutionControl ec = Util.safeCast(executionControl, InstanceManagementExecutionControl.class);
        if (ec == null) {
            LOGGER.error("ExecutionControl is not instance of {}", InstanceManagementExecutionControl.class.getName());
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ExecutionControl is missing"));
            return false;
        }

        String dataSourceId = ec.getDataSource();
        if (dataSourceId == null) {
            LOGGER.error("ExecutionControl.dataSource is missing");
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ExecutionControl.dataSource is missing"));
            return false;
        }

        DataSource dataSource = componentRepository.getComponent(dataSourceId);
        if (dataSource == null) {
            String msg = String.format("Runtime dependency '%s' of type '%s' is not available", dataSourceId, DataSource.class.getName());
            LOGGER.error(msg);
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(msg));
            return false;
        }

        return true;
    }

    private Map<String, Object> registerInstance(
            InstanceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        Instance instance = parseInstance(request);
        if (instance == null) {
            context.getMessages().addMessage(BAD_REQUEST.error("Instance information is missing or malformed"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        // validate instance
        if (isNullOrEmpty(instance.getHost())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'host' is null or empty"));
            return null;
        }

        if (instance.getPort() <= 0) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'port' has invalid value"));
            return null;
        }

        if (isNullOrEmpty(instance.getNamespace())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'namespace' is null or empty"));
            return null;
        }
        if (!namespaceManager.hasNamespace(instance.getNamespace())) {
            String msg = String.format("No such namespace '%s' exists", instance.getNamespace());
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        if (isNullOrEmpty(instance.getApplication())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'application' is null or empty"));
            return null;
        }
        if (!applicationManager.hasApplication(instance.getNamespace(), instance.getApplication())) {
            String msg = String.format(
                    "No such application with '%s' in namespace '%s'",
                    instance.getApplication(),
                    instance.getNamespace()
            );
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        InstanceManager instanceManager = new InstanceManager(dataSource, "system");
        try {
            instanceManager.registerInstance(instance);
            return MapBuilder.linkedHashMap().kv("status", "registered").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> unregisterInstance(
            InstanceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        Instance instance = parseInstance(request);
        if (instance == null) {
            context.getMessages().addMessage(BAD_REQUEST.error("Instance information is missing or malformed"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        // validate instance
        if (isNullOrEmpty(instance.getHost())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'host' is null or empty"));
            return null;
        }

        if (instance.getPort() < 0) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'port' has invalid value"));
            return null;
        }

        if (isNullOrEmpty(instance.getNamespace())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'namespace' is null or empty"));
            return null;
        }
        if (!namespaceManager.hasNamespace(instance.getNamespace())) {
            String msg = String.format("No such namespace '%s' exists", instance.getNamespace());
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        if (isNullOrEmpty(instance.getApplication())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'application' is null or empty"));
            return null;
        }
        if (!applicationManager.hasApplication(instance.getNamespace(), instance.getApplication())) {
            String msg = String.format(
                    "No such application with '%s' in namespace '%s'",
                    instance.getApplication(),
                    instance.getNamespace()
            );
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        InstanceManager instanceManager = new InstanceManager(dataSource, "system");
        try {
            instanceManager.unregisterInstance(instance);
            return MapBuilder.linkedHashMap().kv("status", "unregistered").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> markUpInstance(
            InstanceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        Instance instance = parseInstance(request);
        if (instance == null) {
            context.getMessages().addMessage(BAD_REQUEST.error("Instance information is missing or malformed"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        // validate instance
        if (isNullOrEmpty(instance.getHost())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'host' is null or empty"));
            return null;
        }

        if (instance.getPort() < 0) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'port' has invalid value"));
            return null;
        }

        if (isNullOrEmpty(instance.getNamespace())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'namespace' is null or empty"));
            return null;
        }
        if (!namespaceManager.hasNamespace(instance.getNamespace())) {
            String msg = String.format("No such namespace '%s' exists", instance.getNamespace());
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        if (isNullOrEmpty(instance.getApplication())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'application' is null or empty"));
            return null;
        }
        if (!applicationManager.hasApplication(instance.getNamespace(), instance.getApplication())) {
            String msg = String.format(
                    "No such application with '%s' in namespace '%s'",
                    instance.getApplication(),
                    instance.getNamespace()
            );
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        InstanceManager instanceManager = new InstanceManager(dataSource, "system");
        try {
            instanceManager.markup(instance);
            return MapBuilder.linkedHashMap().kv("status", "markedUp").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> markDownInstance(
            InstanceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        Instance instance = parseInstance(request);
        if (instance == null) {
            context.getMessages().addMessage(BAD_REQUEST.error("Instance information is missing or malformed"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        // validate instance
        if (isNullOrEmpty(instance.getHost())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'host' is null or empty"));
            return null;
        }

        if (instance.getPort() < 0) {
            context.getMessages().addMessage(BAD_REQUEST.error("Fields 'port' has invalid value"));
            return null;
        }

        if (isNullOrEmpty(instance.getNamespace())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'namespace' is null or empty"));
            return null;
        }
        if (!namespaceManager.hasNamespace(instance.getNamespace())) {
            String msg = String.format("No such namespace '%s' exists", instance.getNamespace());
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        if (isNullOrEmpty(instance.getApplication())) {
            context.getMessages().addMessage(BAD_REQUEST.error("Field 'application' is null or empty"));
            return null;
        }
        if (!applicationManager.hasApplication(instance.getNamespace(), instance.getApplication())) {
            String msg = String.format(
                    "No such application with '%s' in namespace '%s'",
                    instance.getApplication(),
                    instance.getNamespace()
            );
            context.getMessages().addMessage(CONFLICT.error(msg));
            return null;
        }

        InstanceManager instanceManager = new InstanceManager(dataSource, "system");
        try {
            instanceManager.markdown(instance);
            return MapBuilder.linkedHashMap().kv("status", "markedDown").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, List<Instance>> findInstances(
            InstanceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        // filters
        Map<String, String> filters = createFilters(request);

        // fetch count and skip count
        Integer fetch = request.firstWithName("fetch").singleValue(Integer.class);
        if (fetch == null || fetch <= 0) {
            fetch = 10;
        }
        Integer skip = request.firstWithName("skip").singleValue(Integer.class);
        if (skip == null || skip <= 0) {
            skip = 0;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        InstanceManager instanceManager = new InstanceManager(dataSource, "system");
        try {
            return MapBuilder.linkedHashMap().kv("instances", instanceManager.findInstances(filters, fetch, skip)).build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }

    }

    private Instance parseInstance(ParamValueHolders request) {
        try {
            JsonNode jsonNode = request.firstWithName("instance").singleValue(JsonNode.class);
            return Helper.MAPPER.treeToValue(jsonNode, Instance.class);
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, String> createFilters(ParamValueHolders request) {
        Map<String, String> filters = new LinkedHashMap<>();
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace != null) {
            filters.put(InstanceManager.Columns.NAMESPACE, namespace);
        }
        String application = request.firstWithName("application").singleValue(String.class);
        if (application != null) {
            filters.put(InstanceManager.Columns.APPLICATION, application);
        }
        String status = request.firstWithName("status").singleValue(String.class);
        if (status != null) {
            filters.put(InstanceManager.Columns.STATUS, status);
        }
        return filters;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
