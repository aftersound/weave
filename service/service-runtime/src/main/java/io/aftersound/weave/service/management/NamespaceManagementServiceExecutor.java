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
import java.util.LinkedHashMap;
import java.util.Map;

import static io.aftersound.weave.service.message.MessageRegistry.*;

public class NamespaceManagementServiceExecutor extends ServiceExecutor<Object> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = NamespaceManagementExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(NamespaceManagementServiceExecutor.class);

    public NamespaceManagementServiceExecutor(ComponentRepository componentRepository) {
        super(componentRepository);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        if (!validate(executionControl, context)) {
            return null;
        }

        NamespaceManagementExecutionControl ec = (NamespaceManagementExecutionControl) executionControl;

        String opName = request.firstWithName("operation").singleValue(String.class);
        Operation op = Operation.byScopeAndName(Scope.Namespace, opName);
        if (op == null) {
            context.getMessages().addMessage(INVALID_PARAMETER_VALUE.error("operation", "String", opName));
            return null;
        }
        switch (op) {
            case CreateNamespace:
                return createNamespace(ec, request, context);
            case GetNamespace:
                return getNamespace(ec, request, context);
            case UpdateNamespace:
                return updateNamespace(ec, request, context);
            case DeleteNamespace:
                return deleteNamespace(ec, request, context);
            case FindNamespaces:
                return findNamespaces(ec, request, context);
            case FindNamespaceHistory:
                return findNamespaceHistory(ec, request, context);
            default:
                context.getMessages().addMessage(BAD_REQUEST.error(String.format("Operation '%s' is not supported", opName)));
                return null;
        }
    }

    private boolean validate(ExecutionControl executionControl, ServiceContext context) {
        NamespaceManagementExecutionControl ec = Util.safeCast(executionControl, NamespaceManagementExecutionControl.class);
        if (ec == null) {
            LOGGER.error("ExecutionControl is not instance of {}", NamespaceManagementExecutionControl.class.getName());
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

    private Object createNamespace(NamespaceManagementExecutionControl ec, ParamValueHolders request, ServiceContext context) {
        JsonNode namespaceJsonNode = request.firstWithName("namespace").singleValue(JsonNode.class);
        if (namespaceJsonNode == null) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }

        Namespace namespace;
        try {
            namespace = Helper.MAPPER.treeToValue(namespaceJsonNode, Namespace.class);
        } catch (Exception e) {
            context.getMessages().addMessage(INVALID_PARAMETER_VALUE.error("namespace", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (namespaceManager.hasNamespace(namespace.getName())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("Namespace '%s' already exists", namespace.getName()))
            );
            return null;
        }

        try {
            namespaceManager.createNamespace(namespace);
            return MapBuilder.linkedHashMap().kv("status", "created").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> updateNamespace(
            NamespaceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        JsonNode namespaceJsonNode = request.firstWithName("namespace").singleValue(JsonNode.class);
        if (namespaceJsonNode == null) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }

        Namespace namespace;
        try {
            namespace = Helper.MAPPER.treeToValue(namespaceJsonNode, Namespace.class);
        } catch (Exception e) {
            context.getMessages().addMessage(INVALID_PARAMETER_VALUE.error("namespace", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(namespace.getName())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such namespace '%s' exists", namespace.getName()))
            );
            return null;
        }

        try {
            namespaceManager.updateNamespace(namespace);
            return MapBuilder.linkedHashMap().kv("status", "updated").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }

    }

    private Namespace getNamespace(
            NamespaceManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        String name = request.firstWithName("name").singleValue(String.class);
        if (name == null || name.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        try {
            Namespace namespace = namespaceManager.getNamespace(name);
            if (namespace != null) {
                return namespace;
            } else {
                context.getMessages().addMessage(
                        MessageRegistry.NOT_FOUND.error(String.format("No namespace with name '%s'", name))
                );
                return null;
            }
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }

    }

    private Map<String, Object> deleteNamespace(
            NamespaceManagementExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        String name = request.firstWithName("name").singleValue(String.class);
        if (name == null || name.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        if (!namespaceManager.hasNamespace(name)) {
            return MapBuilder.linkedHashMap().kv("status", "No namespace found").build();
        }

        try {
            namespaceManager.deleteNamespace(name);
            return MapBuilder.linkedHashMap().kv("status", "deleted").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Object findNamespaces(
            NamespaceManagementExecutionControl ec,
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

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        try {
            return MapBuilder.linkedHashMap().kv("namespaces", namespaceManager.findNamespaces(filters, fetch, skip)).build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Object findNamespaceHistory(
            NamespaceManagementExecutionControl ec,
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

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        try {
            return MapBuilder.linkedHashMap().kv("namespaces", namespaceManager.findNamespaceHistory(filters, fetch, skip)).build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, String> createFilters(ParamValueHolders request) {
        Map<String, String> filters = new LinkedHashMap<>();
        String name = request.firstWithName("name").singleValue(String.class);
        if (name != null) {
            filters.put(NamespaceManager.Columns.NAME, name);
        }
        String owner = request.firstWithName("owner").singleValue(String.class);
        if (owner != null) {
            filters.put(NamespaceManager.Columns.OWNER, owner);
        }
        String ownerEmail = request.firstWithName("ownerEmail").singleValue(String.class);
        if (ownerEmail != null) {
            filters.put(NamespaceManager.Columns.OWNER_EMAIL, ownerEmail);
        }
        String description = request.firstWithName("description").singleValue(String.class);
        if (description != null) {
            filters.put(NamespaceManager.Columns.DESCRIPTION, ownerEmail);
        }
        return filters;
    }

}
