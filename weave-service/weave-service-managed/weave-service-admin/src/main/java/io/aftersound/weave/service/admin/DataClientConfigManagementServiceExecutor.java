package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientRegistry;
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

public class DataClientConfigManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = DataClientConfigManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionManagementServiceExecutor.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    public DataClientConfigManagementServiceExecutor(ManagedResources managedResources) {
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
        if (!(serviceMetadata.getExecutionControl() instanceof DataClientConfigManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        DataClientConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        if (executionControl.getMetadataDirectory() == null) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_SERVICE_METADATA_INFO_UNSPECIFIED.error("ServiceMetadata.executionControl.metadataDirectory")
            );
            return false;
        }

        Path metadataDir = PathHandle.of(executionControl.getMetadataDirectory()).path();
        if (!Files.exists(metadataDir)) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_DIRECTORY_NOT_EXISTS.error(
                            "Metadata",
                            executionControl.getMetadataDirectory()
                    )
            );
            return false;
        }

        return true;
    }

    private Object create(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        JsonNode jsonNode = request.firstWithName("dataClientConfigRequest").singleValue(JsonNode.class);

        JsonNode typeJsonNode = jsonNode.get("type");
        String type = typeJsonNode != null ? typeJsonNode.asText() : null;
        if (type == null || type.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.CREATE_DATA_CLIENT_CONFIG_TYPE_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode idJsonNode = jsonNode.get("id");
        String id = idJsonNode != null ? idJsonNode.asText() : null;
        if (id == null || id.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.CREATE_DATA_CLIENT_CONFIG_ID_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode optionsJsonNode = jsonNode.get("options");
        if (optionsJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.CREATE_DATA_CLIENT_CONFIG_OPTIONS_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        DataClientConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        DataClientRegistry dataClientRegistry = managedResources.getResource(
                DataClientRegistry.class.getName(),
                DataClientRegistry.class
        );

        synchronized (dataClientRegistry) {
            Path metadataPath = Helper.jsonFilePath(metadataDirectory, Helper.format(type, id));
            if (Files.exists(metadataPath)) {
                context.getMessages().addMessage(Messages.CREATE_DATA_CLIENT_CONFIG_ALREADY_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writeValueAsBytes(jsonNode);
                Files.write(metadataPath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to create data client config", this, e);
                context.getMessages().addMessage(Messages.CREATE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object list(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        List<String> targetIds = request.firstWithName("id").multiValues(String.class);
        List<String> targetTypes = request.firstWithName("type").multiValues(String.class);

        DataClientConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();
        Path metadataDir = PathHandle.of(metadataDirectory).path();

        DataClientRegistry dataClientRegistry = managedResources.getResource(
                DataClientRegistry.class.getName(),
                DataClientRegistry.class
        );

        List<Map<String, Object>> dataClientConfigList = new ArrayList<>();

        try {
            DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(metadataDir, Helper.GLOB_PATTERN);
            for (Path path : jsonFiles) {
                JsonNode jsonNode = MAPPER.readTree(path.toFile());

                String type = jsonNode.get("type").asText();
                String id = jsonNode.get("id").asText();

                if ((targetIds.isEmpty() || targetIds.contains(id)) &&
                    (targetTypes.isEmpty() || targetTypes.contains(type))) {

                    boolean activated = (dataClientRegistry.getClient(id) != null);

                    Map<String, Object> dataClientConfigWithStatus = new LinkedHashMap<>();
                    dataClientConfigWithStatus.put("dataClientConfig", jsonNode);
                    dataClientConfigWithStatus.put("status", activated ? "activated" : "unactivated");

                    dataClientConfigList.add(dataClientConfigWithStatus);
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred when attempting to list data client config", this, e);
            context.getMessages().addMessage(Messages.LIST_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", dataClientConfigList);
        return result;
    }

    private Object update(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        JsonNode jsonNode = request.firstWithName("dataClientConfigRequest").singleValue(JsonNode.class);

        JsonNode typeJsonNode = jsonNode.get("type");
        String type = typeJsonNode != null ? typeJsonNode.asText() : null;
        if (type == null || type.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.UPDATE_DATA_CLIENT_CONFIG_TYPE_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode idJsonNode = jsonNode.get("id");
        String id = idJsonNode != null ? idJsonNode.asText() : null;
        if (id == null || id.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.UPDATE_DATA_CLIENT_CONFIG_ID_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode optionsJsonNode = jsonNode.get("options");
        if (optionsJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.UPDATE_DATA_CLIENT_CONFIG_OPTIONS_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        DataClientConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        DataClientRegistry dataClientRegistry = managedResources.getResource(
                DataClientRegistry.class.getName(),
                DataClientRegistry.class
        );

        synchronized (dataClientRegistry) {
            Path jsonFilePath = Helper.jsonFilePath(metadataDirectory, Helper.format(type, id));
            if (!Files.exists(jsonFilePath)) {
                context.getMessages().addMessage(Messages.UPDATE_DATA_CLIENT_CONFIG_NOT_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writeValueAsBytes(jsonNode);
                Files.write(jsonFilePath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to update data client config", this, e);
                context.getMessages().addMessage(Messages.UPDATE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object delete(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        String type = request.firstWithName("type").singleValue(String.class);
        String id = request.firstWithName("id").singleValue(String.class);

        DataClientConfigManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        DataClientRegistry dataClientRegistry = managedResources.getResource(
                DataClientRegistry.class.getName(),
                DataClientRegistry.class
        );

        synchronized (dataClientRegistry) {
            try {
                Path metadataPath = Helper.jsonFilePath(metadataDirectory, Helper.format(type, id));
                if (Files.exists(metadataPath)) {
                    Files.delete(metadataPath);
                } else {
                    context.getMessages().addMessage(Messages.DELETE_DATA_CLIENT_CONFIG_NOT_EXISTS.asWarning());
                }
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to delete data client config", this, e);
                context.getMessages().addMessage(Messages.DELETE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private static class Helper {

        private static final String GLOB_PATTERN = "data-client-*.json";
        private static final String PREFIX = "data-client-";
        private static final String EXTENSION = ".json";

        static String format(String type, String id) {
            return type + "-" + id;
        }

        static Path jsonFilePath(String metadataDirectory, String dataClientConfigName) {
            return PathHandle.of(metadataDirectory + "/" + jsonFileName(dataClientConfigName)).path();
        }

        static String jsonFileName(String dataClientConfigName) {
            return PREFIX + dataClientConfigName + EXTENSION;
        }

        static String dataClientConfigName(String jsonFileName) {
            return jsonFileName.substring(PREFIX.length(), jsonFileName.length() - EXTENSION.length());
        }

    }

}
