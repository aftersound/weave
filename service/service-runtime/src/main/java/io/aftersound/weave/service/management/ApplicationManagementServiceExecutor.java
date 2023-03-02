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

import static io.aftersound.weave.service.message.MessageRegistry.BAD_REQUEST;
import static io.aftersound.weave.service.message.MessageRegistry.CONFLICT;

public class ApplicationManagementServiceExecutor extends ServiceExecutor<Object> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = ApplicationManagementExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationManagementServiceExecutor.class);

    public ApplicationManagementServiceExecutor(ComponentRepository componentRepository) {
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

        ApplicationManagementExecutionControl ec = (ApplicationManagementExecutionControl) executionControl;

        String opName = request.firstWithName("operation").singleValue(String.class);
        Operation op = Operation.byScopeAndName(Scope.Application, opName);
        if (op == null) {
            context.getMessages().addMessage(MessageRegistry.INVALID_PARAMETER_VALUE.error("operation", "String", opName));
            return null;
        }
        switch (op) {
            case CreateApplication:
                return createApplication(ec, request, context);
            case GetApplication:
                return getApplication(ec, request, context);
            case UpdateApplication:
                return updateApplication(ec, request, context);
            case DeleteApplication:
                return deleteApplication(ec, request, context);
            case FindApplications:
                return findApplications(ec, request, context);
            case FindApplicationHistory:
                return findApplicationHistory(ec, request, context);
            default:
                context.getMessages().addMessage(BAD_REQUEST.error(String.format("Operation '%s' is not supported", opName)));
                return null;
        }
    }

    private boolean validate(ExecutionControl executionControl, ServiceContext context) {
        ApplicationManagementExecutionControl ec = Util.safeCast(executionControl, ApplicationManagementExecutionControl.class);
        if (ec == null) {
            LOGGER.error("ExecutionControl is not instance of {}", ApplicationManagementExecutionControl.class.getName());
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

    private Object createApplication(ApplicationManagementExecutionControl ec, ParamValueHolders request, ServiceContext context) {
        JsonNode appJsonNode = request.firstWithName("application").singleValue(JsonNode.class);
        if (appJsonNode == null) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("application", "Request"));
            return null;
        }

        Application app;
        try {
            app = Helper.MAPPER.treeToValue(appJsonNode, Application.class);
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INVALID_PARAMETER_VALUE.error("application", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        if (!namespaceManager.hasNamespace(app.getNamespace())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such namespace '%s'", app.getNamespace()))
            );
            return null;
        }

        if (applicationManager.hasApplication(app.getNamespace(), app.getName())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("Application '%s' in namespace '%s' already exists", app.getName(), app.getNamespace()))
            );
            return null;
        }

        try {
            applicationManager.createApplication(app);
            return MapBuilder.linkedHashMap().kv("status", "created").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, Object> updateApplication(
            ApplicationManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        JsonNode appJsonNode = request.firstWithName("application").singleValue(JsonNode.class);
        if (appJsonNode == null) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("application", "Request"));
            return null;
        }

        Application app;
        try {
            app = Helper.MAPPER.treeToValue(appJsonNode, Application.class);
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INVALID_PARAMETER_VALUE.error("application", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());

        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        if (!namespaceManager.hasNamespace(app.getNamespace())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such namespace '%s'", app.getNamespace()))
            );
            return null;
        }

        if (!applicationManager.hasApplication(app.getNamespace(), app.getName())) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such application with '%s' in namespace '%s'", app.getName(), app.getNamespace()))
            );
            return null;
        }

        try {
            applicationManager.updateApplication(app);
            return MapBuilder.linkedHashMap().kv("status", "updated").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }

    }

    private Application getApplication(
            ApplicationManagementExecutionControl control,
            ParamValueHolders request,
            ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }
        String name = request.firstWithName("name").singleValue(String.class);
        if (name == null || name.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(control.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such namespace '%s'", namespace))
            );
            return null;
        }

        try {
            Application application = applicationManager.getApplication(namespace, name);
            if (application != null) {
                return application;
            } else {
                context.getMessages().addMessage(
                        MessageRegistry.NOT_FOUND.error(String.format("No application with name '%s' in namespace '%s'", name, namespace))
                );
                return null;
            }
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }

    }

    private Map<String, Object> deleteApplication(
            ApplicationManagementExecutionControl ec,
            ParamValueHolders request,
            ServiceContext context) {
        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace == null || namespace.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("namespace", "Request"));
            return null;
        }

        String name = request.firstWithName("name").singleValue(String.class);
        if (name == null || name.isEmpty()) {
            context.getMessages().addMessage(MessageRegistry.MISSING_REQUIRED_PARAMETER.error("name", "Request"));
            return null;
        }

        DataSource dataSource = componentRepository.getComponent(ec.getDataSource());
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");

        if (!namespaceManager.hasNamespace(namespace)) {
            context.getMessages().addMessage(
                    CONFLICT.error(String.format("No such namespace '%s'", namespace))
            );
            return null;
        }

        if (!applicationManager.hasApplication(namespace, name)) {
            return MapBuilder.linkedHashMap().kv("status", "No such application").build();
        }

        try {
            applicationManager.deleteApplication(namespace, name);
            return MapBuilder.linkedHashMap().kv("status", "deleted").build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Object findApplications(
            ApplicationManagementExecutionControl ec,
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
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        try {
            return MapBuilder.linkedHashMap().kv("applications", applicationManager.findApplications(filters, fetch, skip)).build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Object findApplicationHistory(
            ApplicationManagementExecutionControl ec,
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
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        try {
            return MapBuilder.linkedHashMap().kv("applications", applicationManager.findApplicationHistory(filters, fetch, skip)).build();
        } catch (Exception e) {
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error(e.getMessage()));
            return null;
        }
    }

    private Map<String, String> createFilters(ParamValueHolders request) {
        Map<String, String> filters = new LinkedHashMap<>();

        String namespace = request.firstWithName("namespace").singleValue(String.class);
        if (namespace != null) {
            filters.put(ApplicationManager.Columns.NAMESPACE, namespace);
        }
        String name = request.firstWithName("name").singleValue(String.class);
        if (name != null) {
            filters.put(ApplicationManager.Columns.NAME, name);
        }
        String owner = request.firstWithName("owner").singleValue(String.class);
        if (owner != null) {
            filters.put(ApplicationManager.Columns.OWNER, owner);
        }
        String ownerEmail = request.firstWithName("ownerEmail").singleValue(String.class);
        if (ownerEmail != null) {
            filters.put(ApplicationManager.Columns.OWNER_EMAIL, ownerEmail);
        }
        String description = request.firstWithName("description").singleValue(String.class);
        if (ownerEmail != null) {
            filters.put(ApplicationManager.Columns.DESCRIPTION, description);
        }

        return filters;
    }
}
