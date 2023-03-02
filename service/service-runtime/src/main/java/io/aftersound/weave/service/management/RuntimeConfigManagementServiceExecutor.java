package io.aftersound.weave.service.management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
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
import java.util.List;
import java.util.Map;

import static io.aftersound.weave.service.message.MessageRegistry.BAD_REQUEST;

public class RuntimeConfigManagementServiceExecutor extends ServiceExecutor<Object> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = RuntimeConfigManagementExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigManagementServiceExecutor.class);

    public RuntimeConfigManagementServiceExecutor(ComponentRepository componentRepository) {
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

        RuntimeConfigManagementExecutionControl ec = (RuntimeConfigManagementExecutionControl) executionControl;

        String opName = request.firstWithName("operation").singleValue(String.class);
        Operation op = Operation.byScopeAndName(Scope.RuntimeConfig, opName);
        if (op == null) {
            context.getMessages().addMessage(MessageRegistry.INVALID_PARAMETER_VALUE.error("operation", "String", opName));
            return null;
        }
        switch (op) {
            case CreateRuntimeConfig:
                return createRuntimeConfig(ec, request, context);
            case GetRuntimeConfig:
                return getRuntimeConfig(ec, request, context);
            case UpdateRuntimeConfig:
                return updateRuntimeConfig(ec, request, context);
            case DeleteRuntimeConfig:
                return deleteRuntimeConfig(ec, request, context);
            case GetRuntimeSubconfig:
                return getRuntimeSubconfig(ec, request, context);
            default: {
                context.getMessages().addMessage(BAD_REQUEST.error(String.format("Operation '%s' is not supported", opName)));
                return null;
            }
        }
    }

    private boolean validate(ExecutionControl executionControl, ServiceContext context) {
        RuntimeConfigManagementExecutionControl ec = Util.safeCast(executionControl, RuntimeConfigManagementExecutionControl.class);
        if (ec == null) {
            LOGGER.error("ExecutionControl is not instance of {}", RuntimeConfigManagementExecutionControl.class.getName());
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ExecutionControl is missing"));
            return false;
        }

        List<String> configIdentifiers = ec.getSubconfigIdentifiers();
        if (configIdentifiers == null || configIdentifiers.isEmpty()) {
            LOGGER.error("ExecutionControl.configIdentifiers is missing or empty");
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ExecutionControl.configIdentifiers is missing or empty"));
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

    private boolean validateRuntimeConfig(JsonNode runtimeConfigJsonNode, List<String> subconfigIdentifiers, ServiceContext context) {
        boolean validated = true;
        for (String subconfigIdentifier : subconfigIdentifiers) {
            JsonNode subconfigJsonNode = runtimeConfigJsonNode.get(subconfigIdentifier);
            if (subconfigJsonNode == null && subconfigJsonNode.getNodeType() != JsonNodeType.ARRAY) {
                context.getMessages().addMessage(
                        MessageRegistry.MISSING_REQUIRED_PARAMETER.error("runtimeConfig.'" + subconfigIdentifier + "'", "Array")
                );
                validated = false;
            }
        }
        return validated;
    }

    private Object createRuntimeConfig(RuntimeConfigManagementExecutionControl ec, ParamValueHolders request, ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String appName = request.firstWithName("name").singleValue(String.class);
        if (appName == null || appName.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        JsonNode runtimeConfigJsonNode = request.firstWithName("runtimeConfig").singleValue(JsonNode.class);
        if (!validateRuntimeConfig(runtimeConfigJsonNode, ec.getSubconfigIdentifiers(), context)) {
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such namespace exists"));
            return null;
        }

        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        if (!applicationManager.hasApplication(namespace, appName)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such application exists"));
            return null;
        }

        RuntimeConfigManager runtimeConfigManager = new RuntimeConfigManager(dataSource, ec.table(), "system");
        try {
            String fullQualifiedApp = String.format("%s:%s", namespace, appName);
            runtimeConfigManager.createRuntimeConfig(fullQualifiedApp, runtimeConfigJsonNode);
            return MapBuilder.linkedHashMap().kv("status", "created").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> getRuntimeConfig(
            RuntimeConfigManagementExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String appName = request.firstWithName("name").singleValue(String.class);
        if (appName == null || appName.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such namespace exists"));
            return null;
        }

        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        if (!applicationManager.hasApplication(namespace, appName)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such application exists"));
            return null;
        }

        RuntimeConfigManager runtimeConfigManager = new RuntimeConfigManager(dataSource, ec.table(), "system");
        try {
            String fullQualifiedApp = String.format("%s:%s", namespace, appName);
            return runtimeConfigManager.getRuntimeConfig(fullQualifiedApp);
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Object updateRuntimeConfig(RuntimeConfigManagementExecutionControl ec, ParamValueHolders request, ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String appName = request.firstWithName("name").singleValue(String.class);
        if (appName == null || appName.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }
        JsonNode runtimeConfigJsonNode = request.firstWithName("runtimeConfig").singleValue(JsonNode.class);
        if (!validateRuntimeConfig(runtimeConfigJsonNode, ec.getSubconfigIdentifiers(), context)) {
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such namespace"));
            return null;
        }

        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        if (!applicationManager.hasApplication(namespace, appName)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such application"));
            return null;
        }

        RuntimeConfigManager runtimeConfigManager = new RuntimeConfigManager(dataSource, ec.table(), "system");
        try {
            String fullQualifiedApp = String.format("%s:%s", namespace, appName);
            runtimeConfigManager.updateRuntimeConfig(fullQualifiedApp, runtimeConfigJsonNode);
            return MapBuilder.linkedHashMap().kv("status", "updated").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> deleteRuntimeConfig(
            RuntimeConfigManagementExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String appName = request.firstWithName("name").singleValue(String.class);
        if (appName == null || appName.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such namespace"));
            return null;
        }

        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        if (!applicationManager.hasApplication(namespace, appName)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such application"));
            return null;
        }

        RuntimeConfigManager runtimeConfigManager = new RuntimeConfigManager(dataSource, ec.table(), "system");
        String fullQualifiedApp = String.format("%s:%s", namespace, appName);
        Map<String, Object> runtimeConfig = runtimeConfigManager.getRuntimeConfig(fullQualifiedApp);
        if (runtimeConfig == null) {
            return MapBuilder.linkedHashMap().kv("status", "No runtime config found").build();
        } else {
            try {
                runtimeConfigManager.deleteRuntimeConfig(fullQualifiedApp);
                return MapBuilder.linkedHashMap().kv("status", "deleted").build();
            } catch (Exception e) {
                context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
                return null;
            }
        }
    }

    private Object getRuntimeSubconfig(
            RuntimeConfigManagementExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String appName = request.firstWithName("name").singleValue(String.class);
        if (appName == null || appName.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        String configIdentifier = request.firstWithName("configIdentifier").singleValue(String.class);
        if (configIdentifier == null || configIdentifier.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("configIdentifier", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such namespace"));
            return null;
        }

        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        if (!applicationManager.hasApplication(namespace, appName)) {
            context.getMessages().addMessage(MessageRegistry.CONFLICT.error("No such application"));
            return null;
        }

        RuntimeConfigManager runtimeConfigManager = new RuntimeConfigManager(dataSource, ec.table(), "system");
        String fullQualifiedApp = String.format("%s:%s", namespace, appName);
        Map<String, Object> runtimeConfig = runtimeConfigManager.getRuntimeConfig(fullQualifiedApp);
        Object subconfig = runtimeConfig != null ? runtimeConfig.get(configIdentifier) : null;
        if (subconfig != null) {
            return subconfig;
        } else {
            context.getMessages().addMessage(
                    MessageRegistry.NOT_FOUND.error("Config found")
            );
            return null;
        }
    }

}
