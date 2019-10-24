package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.file.PathHandle;
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

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BatchAppConfigManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = BatchAppConfigManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchAppConfigManagementServiceExecutor.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    public BatchAppConfigManagementServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        if (!validate(serviceMetadata, context)) {
            return null;
        }

        String operation = request.firstWithName("operation").singleValue(String.class);

        if ("create".equals(operation)) {
            return create(serviceMetadata, request, context);
        }

        if ("list".equals(operation)) {
            return list(serviceMetadata, request, context);
        }

        if ("update".equals(operation)) {
            return update(serviceMetadata, request, context);
        }

        if ("delete".equals(operation)) {
            return delete(serviceMetadata, request, context);
        }

        return list(serviceMetadata, request, context);
    }

    private boolean validate(ServiceMetadata serviceMetadata, ServiceContext context) {
        if (!(serviceMetadata.getExecutionControl() instanceof BatchAppConfigManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        BatchAppConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        if (executionControl.getAppConfigDirectory() == null) {
            context.getMessages().addMessage(Messages.BATCH_APP_CONFIG_DIRECTORY_UNSPECIFIED);
            return false;
        }

        Path dir = PathHandle.of(executionControl.getAppConfigDirectory()).path();
        if (!Files.exists(dir)) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_BATCH_APP_CONFIG_DIRECTORY_NOT_EXISTS.error(executionControl.getAppConfigDirectory())
            );
            return false;
        }

        return true;
    }

    private Object create(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        JsonNode jsonNode = request.firstWithName("batchAppConfigRequest").singleValue(JsonNode.class);
        if (!validateBatchAppConfigRequest(jsonNode, context)) {
            return Result.FAILURE;
        }

        String id = jsonNode.get("id").asText();
        JsonNode configJsonNode = jsonNode.get("config");

        BatchAppConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String appConfigDirectory = executionControl.getAppConfigDirectory();

        synchronized (appConfigDirectory) {
            Path jsonFilePath = Helper.jsonFilePath(appConfigDirectory, id);
            if (Files.exists(jsonFilePath)) {
                context.getMessages().addMessage(Messages.CREATE_BATCH_APP_CONFIG_ALREADY_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(configJsonNode);
                Files.write(jsonFilePath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to create batch app config", this, e);
                context.getMessages().addMessage(Messages.CREATE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    public boolean validateBatchAppConfigRequest(JsonNode jsonNode, ServiceContext context) {
        JsonNode idJsonNode = jsonNode.get("id");
        String id = idJsonNode != null ? idJsonNode.asText() : null;
        if (id == null || id.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("id")
            );
            return false;
        }

        JsonNode configJsonNode = jsonNode.get("config");
        if (configJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config")
            );
            return false;
        }

        if (configJsonNode.get("springDataSourceConfig") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.springDataSourceConfig")
            );
            return false;
        }

        if (configJsonNode.get("jsonSpecTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.jsonSpecTypes")
            );
            return false;
        }

        if (!configJsonNode.get("jsonSpecTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.jsonSpecTypes")
            );
            return false;
        }

        if (configJsonNode.get("dataSourceControlTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.dataSourceControlTypes")
            );
            return false;
        }

        if (!configJsonNode.get("dataSourceControlTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.dataSourceControlTypes")
            );
            return false;
        }

        if (configJsonNode.get("extractControlTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.extractControlTypes")
            );
            return false;
        }

        if (!configJsonNode.get("extractControlTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.extractControlTypes")
            );
            return false;
        }

        if (configJsonNode.get("transformControlTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.transformControlTypes")
            );
            return false;
        }

        if (!configJsonNode.get("transformControlTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.transformControlTypes")
            );
            return false;
        }

        if (configJsonNode.get("loadControlTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.loadControlTypes")
            );
            return false;
        }

        if (!configJsonNode.get("loadControlTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.loadControlTypes")
            );
            return false;
        }

        if (configJsonNode.get("dataClientFactoryTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.dataClientFactoryTypes")
            );
            return false;
        }

        if (!configJsonNode.get("dataClientFactoryTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.dataClientFactoryTypes")
            );
            return false;
        }

        if (configJsonNode.get("fileHandlerTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.fileHandlerTypes")
            );
            return false;
        }

        if (!configJsonNode.get("fileHandlerTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.fileHandlerTypes")
            );
            return false;
        }

        if (configJsonNode.get("fileFilterTypes") == null) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_MISSING_FIELD.error("config.fileFilterTypes")
            );
            return false;
        }

        if (!configJsonNode.get("fileFilterTypes").isArray()) {
            context.getMessages().addMessage(
                    Messages.BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY.error("config.fileFilterTypes")
            );
            return false;
        }

        return true;
    }

    private Object list(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        List<String> targetIds = request.firstWithName("id").multiValues(String.class);

        BatchAppConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String appConfigDirectory = executionControl.getAppConfigDirectory();

        List<Map<String, Object>> appConfigList = new ArrayList<>();
        try {
            Path dir = PathHandle.of(appConfigDirectory).path();
            DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(dir, Helper.GLOB_PATTERN);
            for (Path path : jsonFiles) {
                JsonNode jsonNode = MAPPER.readTree(path.toFile());
                String id = Helper.id(path.getFileName().toString());

                if (targetIds.isEmpty() || targetIds.contains(id)) {
                    Map<String, Object> appConfig = new LinkedHashMap<>();
                    appConfig.put("id", id);
                    appConfig.put("config", jsonNode);
                    appConfigList.add(appConfig);
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred when attempting to list batch app config", this, e);
            context.getMessages().addMessage(Messages.LIST_BATCH_APP_CONFIG_EXCEPTION_OCCURRED);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", appConfigList);
        return result;
    }

    private Object update(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        JsonNode jsonNode = request.firstWithName("batchAppConfigRequest").singleValue(JsonNode.class);
        if (!validateBatchAppConfigRequest(jsonNode, context)) {
            return Result.FAILURE;
        }

        String id = jsonNode.get("id").asText();
        JsonNode configJsonNode = jsonNode.get("config");

        BatchAppConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String appConfigDirectory = executionControl.getAppConfigDirectory();

        synchronized (appConfigDirectory) {
            Path jsonFilePath = Helper.jsonFilePath(appConfigDirectory, id);
            if (!Files.exists(jsonFilePath)) {
                context.getMessages().addMessage(Messages.UPDATE_BATCH_APP_CONFIG_NOT_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(configJsonNode);
                Files.write(jsonFilePath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to update batch app config", this, e);
                context.getMessages().addMessage(Messages.UPDATE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object delete(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        String id = request.firstWithName("id").singleValue(String.class);

        BatchAppConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String appConfigDirectory = executionControl.getAppConfigDirectory();

        synchronized (appConfigDirectory) {
            try {
                Path jsonFilePath = Helper.jsonFilePath(appConfigDirectory, id);
                if (Files.exists(jsonFilePath)) {
                    Files.delete(jsonFilePath);
                } else {
                    context.getMessages().addMessage(Messages.DELETE_BATCH_APP_CONFIG_NOT_EXISTS.asWarning());
                }
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to delete batch app config", this, e);
                context.getMessages().addMessage(Messages.DELETE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private static class Helper {

        private static final String GLOB_PATTERN = "*.json";
        private static final String EXTENSION = ".json";

        static Path jsonFilePath(String directory, String id) {
            return PathHandle.of(directory + "/" + jsonFileName(id)).path();
        }

        static String jsonFileName(String id) {
            return id + EXTENSION;
        }

        static String id(String fileName) {
            return fileName.substring(0, fileName.length() - EXTENSION.length());
        }

    }
}
