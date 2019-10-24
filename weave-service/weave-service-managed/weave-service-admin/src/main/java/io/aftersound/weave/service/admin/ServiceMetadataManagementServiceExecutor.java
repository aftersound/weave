package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.ServiceMetadataRegistry;
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


public class ServiceMetadataManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = ServiceMetadataManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataManagementServiceExecutor.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    public ServiceMetadataManagementServiceExecutor(ManagedResources managedResources) {
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
        if (!(serviceMetadata.getExecutionControl() instanceof ServiceMetadataManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        ServiceMetadataManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
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
        JsonNode jsonNode = request.firstWithName("serviceMetadataRequest")
                .singleValue(JsonNode.class);

        JsonNode idJsonNode = jsonNode.get("id");
        String id = idJsonNode != null ? idJsonNode.asText() : null;
        if (id == null || id.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.CREATE_SERVICE_METADATA_ID_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode executionControlJsonNode = jsonNode.get("executionControl");
        if (executionControlJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.CREATE_SERVICE_METADATA_EXECUTION_CONTROL_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode typeJsonNode = executionControlJsonNode.get("type");
        String type = typeJsonNode != null ? typeJsonNode.asText() : null;
        if (type == null || type.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.CREATE_SERVICE_METADATA_EXECUTION_CONTROL_TYPE_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode paramFieldsJsonNode = jsonNode.get("paramFields");
        if (paramFieldsJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.CREATE_SERVICE_METADATA_PARAM_FIELDS_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        ServiceMetadataManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        ServiceMetadataRegistry serviceMetadataRegistry = managedResources.getResource(
                ServiceMetadataRegistry.class.getName(),
                ServiceMetadataRegistry.class
        );

        synchronized (serviceMetadataRegistry) {
            String name = Helper.format(id);
            Path metadataPath = Helper.jsonFilePath(metadataDirectory, name);
            if (Files.exists(metadataPath)) {
                context.getMessages().addMessage(Messages.CREATE_SERVICE_METADATA_ALREADY_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonNode);
                Files.write(metadataPath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to create service metadata", this, e);
                context.getMessages().addMessage(Messages.CREATE_SERVICE_METADATA_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object list(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        List<String> targetIds = request.firstWithName("id").multiValues(String.class);
        List<String> targetTypes = request.firstWithName("type").multiValues(String.class);

        ServiceMetadataManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        ServiceMetadataRegistry serviceMetadataRegistry = managedResources.getResource(
                ServiceMetadataRegistry.class.getName(),
                ServiceMetadataRegistry.class
        );

        List<Map<String, Object>> serviceMetadataList = new ArrayList<>();
        try {
            Path metadataDir = PathHandle.of(metadataDirectory).path();
            DirectoryStream<Path> jsonFiles = Files.newDirectoryStream(metadataDir, Helper.GLOB_PATTERN);
            for (Path path : jsonFiles) {
                JsonNode jsonNode = MAPPER.readTree(path.toFile());

                String id = jsonNode.get("id").asText();
                String type = jsonNode.get("executionControl").get("type").asText();

                if ((targetIds.isEmpty() || targetIds.contains(id)) &&
                    (targetTypes.isEmpty() || targetTypes.contains(type))) {

//                    ServiceMetadata activated = serviceMetadataRegistry.getServiceMetadata(id);

                    Map<String, Object> metadataWithStatus = new LinkedHashMap<>();
                    metadataWithStatus.put("serviceMetadata", jsonNode);
                    metadataWithStatus.put("status", "TODO");

                    serviceMetadataList.add(metadataWithStatus);
                }
            }
        } catch (Exception e) {
            LOGGER.error("{}: Exception occurred when attempting to list service metadata", this, e);
            context.getMessages().addMessage(Messages.LIST_SERVICE_METADATA_EXCEPTION_OCCURRED);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", serviceMetadataList);
        return result;
    }

    private Object update(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        JsonNode jsonNode = request.firstWithName("serviceMetadataRequest")
                .singleValue(JsonNode.class);

        JsonNode idJsonNode = jsonNode.get("id");
        String id = idJsonNode != null ? idJsonNode.asText() : null;
        if (id == null || id.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.UPDATE_SERVICE_METADATA_ID_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode executionControlJsonNode = jsonNode.get("executionControl");
        if (executionControlJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.UPDATE_SERVICE_METADATA_EXECUTION_CONTROL_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode typeJsonNode = executionControlJsonNode.get("type");
        String type = typeJsonNode != null ? typeJsonNode.asText() : null;
        if (type == null || type.isEmpty()) {
            context.getMessages().addMessage(
                    Messages.UPDATE_SERVICE_METADATA_EXECUTION_CONTROL_TYPE_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        JsonNode paramFieldsJsonNode = jsonNode.get("paramFields");
        if (paramFieldsJsonNode == null) {
            context.getMessages().addMessage(
                    Messages.UPDATE_SERVICE_METADATA_PARAM_FIELDS_UNSPECIFIED
            );
            return Result.FAILURE;
        }

        ServiceMetadataManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        ServiceMetadataRegistry serviceMetadataRegistry = managedResources.getResource(
                ServiceMetadataRegistry.class.getName(),
                ServiceMetadataRegistry.class
        );

        synchronized (serviceMetadataRegistry) {
            Path jsonFilePath = Helper.jsonFilePath(metadataDirectory, Helper.format(id));
            if (!Files.exists(jsonFilePath)) {
                context.getMessages().addMessage(Messages.UPDATE_SERVICE_METADATA_NOT_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] bytes = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsBytes(jsonNode);
                Files.write(jsonFilePath, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to update service metadata", this, e);
                context.getMessages().addMessage(Messages.UPDATE_SERVICE_METADATA_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object delete(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        String target = request.firstWithName("id").singleValue(String.class);

        ServiceMetadataManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String metadataDirectory = executionControl.getMetadataDirectory();

        ServiceMetadataRegistry serviceMetadataRegistry = managedResources.getResource(
                ServiceMetadataRegistry.class.getName(),
                ServiceMetadataRegistry.class
        );

        synchronized (serviceMetadataRegistry) {
            try {
                Path metadataPath = Helper.jsonFilePath(metadataDirectory, Helper.format(target));
                if (Files.exists(metadataPath)) {
                    Files.delete(metadataPath);
                } else {
                    context.getMessages().addMessage(Messages.DELETE_SERVICE_METADATA_NOT_EXISTS.asWarning());
                }
                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("{}: Exception occurred when attempting to delete service metadata", this, e);
                context.getMessages().addMessage(Messages.DELETE_SERVICE_METADATA_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private static class Helper {

        private static final String GLOB_PATTERN = "service-*.json";
        private static final String PREFIX = "service-";
        private static final String EXTENSION = ".json";

        static String format(String id) {
            return id.replace('/', '#').replace('{', '_').replace('}', '_');
        }

        static Path jsonFilePath(String metadataDirectory, String serviceName) {
            return PathHandle.of(metadataDirectory + "/" + jsonFileName(serviceName)).path();
        }

        static String jsonFileName(String serviceName) {
            return PREFIX + serviceName + EXTENSION;
        }

    }

}
