package io.aftersound.weave.service.admin;

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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExtensionManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = ExtensionManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionManagementServiceExecutor.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperBuilder.forJson().build();

    public ExtensionManagementServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        if (!validate(serviceMetadata, request, context)) {
            return null;
        }

        ExtensionManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        String method = request.firstWithName("operation").singleValue(String.class);

        if ("install".equals(method)) {
            return install(executionControl, request, context);
        }

        if ("list".equals(method)) {
            return list(executionControl, request, context);
        }

        if ("uninstall".equals(method)) {
            return uninstall(executionControl, request, context);
        }

        return list(executionControl, request, context);
    }

    private boolean validate(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        if (!(serviceMetadata.getExecutionControl() instanceof ExtensionManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        ExtensionManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();

        if (executionControl.getExtensionDirectory() == null) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_SERVICE_METADATA_INFO_UNSPECIFIED.error("ServiceMetadata.executionControl.extensionDirectory")
            );
            return false;
        }

        Path dir = PathHandle.of(executionControl.getExtensionDirectory()).path();
        if (!Files.exists(dir)) {
            context.getMessages().add(
                    Messages.TEMPLATE_DIRECTORY_NOT_EXISTS.error(
                            "Extension",
                            executionControl.getExtensionDirectory()
                    )
            );
            return false;
        }

        if (executionControl.getExtensionCategories() == null || executionControl.getExtensionCategories().isEmpty()) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_SERVICE_METADATA_INFO_UNSPECIFIED.error("ServiceMetadata.executionControl.extensionCategories")
            );
            return false;
        }

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );
        if (libraryRegistry == null) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_LIBRARY_REGISTRY_NOT_INITIALIZED.error(scope + "-" + type)
            );
            return false;
        }

        return true;
    }

    private Object install(
            ExtensionManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        String repository = request.firstWithName("repository").singleValue(String.class);
        String groupId = request.firstWithName("groupId").singleValue(String.class);
        String artifactId = request.firstWithName("artifactId").singleValue(String.class);
        String version = request.firstWithName("version").singleValue(String.class);

        String extensionDirectory = PathHandle.of(executionControl.getExtensionDirectory()).path().toString();

        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );
        synchronized (libraryRegistry) {
            try {
                // check if library is already installed
                LibraryInfo library = libraryRegistry.getLibrary(groupId, artifactId, version);
                if (library != null) {
                    context.getMessages().addMessage(
                            Messages.TEMPLATE_LIBRARY_ALREADY_INSTALLED.error(
                                    groupId + ":" + artifactId + ":" + version
                            )
                    );
                    return Result.FAILURE;
                }

                // resolve library source location
                Path librarySourceLocalPath = new LibraryResolver(repository).resolve(groupId, artifactId, version);

                // try to extract information of extensions
                List<ExtensionInfo> extensions = new ArrayList<>();
                for (String extensionCategory : executionControl.getExtensionCategories()) {
                    ExtensionInfo extensionInfo = extractExtensionInfo(extensionCategory, librarySourceLocalPath);
                    if (extensionInfo != null) {
                        extensions.add(extensionInfo);
                    }
                }
                if (extensions.isEmpty()) {
                    context.getMessages().addMessage(
                            Messages.TEMPLATE_LIBRARY_NO_EXTENSION.error(
                                    groupId + ":" + artifactId + ":" + version
                            )
                    );
                    return Result.FAILURE;
                }

                // copy library to target extension directory
                Path libraryPath = copyLibraryToTargetDirectory(librarySourceLocalPath, extensionDirectory);

                // add library into into registry
                libraryRegistry.addLibrary(
                        groupId,
                        artifactId,
                        version,
                        createLibraryInfo(libraryPath, groupId, artifactId, version, extensions)
                );

                // write library registry into file for instance restart
                writeLibraryRegistryToFile(extensionDirectory, libraryRegistry);

                // write extension types into file for instance restart
                for (String extensionCategory : executionControl.getExtensionCategories()) {
                    writeExtensionTypesFile(extensionDirectory, extensionCategory, libraryRegistry);
                }

                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("failed to install extension", e);

                context.getMessages().addMessage(
                    Messages.INSTALL_LIBRARY_EXCEPTION_OCCURRED
                );
                return Result.FAILURE;
            }
        }
    }

    private static ExtensionInfo extractExtensionInfo(String category, Path libraryPath) {
        ZipFile zipFile = null;
        try {
            File file = new File(libraryPath.toAbsolutePath().toString());
            zipFile = new ZipFile(file);
            ZipEntry zipEntry = zipFile.getEntry("META-INF/weave/" + category + "-extensions.json");
            return OBJECT_MAPPER.readValue(
                    zipFile.getInputStream(zipEntry),
                    ExtensionInfo.class
            );
        } catch (Exception e) {
            return null;
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (Exception e) {
                }
            }
        }
    }

    private static Path copyLibraryToTargetDirectory(
            Path librarySourceLocalPath,
            String targetDirectory) throws Exception {
        Path target = PathHandle.of(targetDirectory + "/" + librarySourceLocalPath.toFile().getName()).path();
        Files.copy(librarySourceLocalPath, target);
        return target;
    }

    private LibraryInfo createLibraryInfo(
            Path libraryPath,
            String groupId,
            String artifactId,
            String version,
            List<ExtensionInfo> extensions) {
        LibraryInfo library = new LibraryInfo();
        library.setPath(libraryPath.getFileName().toString());

        Map<String, String> description = new HashMap<>();
        description.put("groupId", groupId);
        description.put("artifactId", artifactId);
        description.put("version", version);
        library.setDescription(description);

        library.setExtensions(extensions);

        return library;
    }

    private static void writeLibraryRegistryToFile(
            String targetDirectory,
            LibraryRegistry libraryRegistry) throws Exception {

        Path jsonFilePath = Paths.get(targetDirectory, "_libraries.json");
        File file = new File(jsonFilePath.toString());
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, libraryRegistry.getLibraries());
    }

    private static void writeExtensionTypesFile(
            String targetDirectory,
            String extensionCategory,
            LibraryRegistry libraryRegistry) throws Exception {
        List<String> extensionTypes = new ArrayList<>();
        for (LibraryInfo library : libraryRegistry.getLibraries()) {
            for (ExtensionInfo extension : library.getExtensions()) {
                if (extensionCategory.equals(extension.getCategory())) {
                    extensionTypes.addAll(extension.getTypes());
                }
            }
        }

        Path jsonFilePath = Paths.get(targetDirectory, "_" + extensionCategory + "-types.json");
        File file = new File(jsonFilePath.toString());
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, extensionTypes);
    }

    private Object list(
            ExtensionManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        String groupId = request.firstWithName("groupId").singleValue(String.class);
        String artifactId = request.firstWithName("artifactId").singleValue(String.class);
        String version = request.firstWithName("version").singleValue(String.class);

        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );

        Collection<LibraryInfo> libraries = libraryRegistry.findLibraries(groupId, artifactId, version);
        Map<String, Object> result = new HashMap<>();
        result.put("list", libraries);
        return result;
    }

    private Object uninstall(
            ExtensionManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        String groupId = request.firstWithName("groupId").singleValue(String.class);
        String artifactId = request.firstWithName("artifactId").singleValue(String.class);
        String version = request.firstWithName("version").singleValue(String.class);

        String extensionDirectory = PathHandle.of(executionControl.getExtensionDirectory()).path().toString();

        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );
        synchronized (libraryRegistry) {
            try {
                LibraryInfo library = libraryRegistry.getLibrary(groupId, artifactId, version);
                if (library == null) {
                    context.getMessages().addMessage(
                            Messages.UNINSTALL_LIBRARY_NOT_FOUND
                    );
                    return Result.FAILURE;
                }

                Path libraryPath = Paths.get(extensionDirectory, library.getPath());
                if (Files.exists(libraryPath)) {
                    Files.delete(libraryPath);
                }

                // remove library info after library file is removed from extension directory
                libraryRegistry.removeLibrary(groupId, artifactId, version);

                // write library registry into file for instance restart
                writeLibraryRegistryToFile(extensionDirectory, libraryRegistry);

                // write extension typs into file for instance restart
                for (String extensionCategory : executionControl.getExtensionCategories()) {
                    writeExtensionTypesFile(extensionDirectory, extensionCategory, libraryRegistry);
                }

                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("failed to delete extension", e);

                context.getMessages().addMessage(
                        Messages.DELETE_LIBRARY_EXCEPTION_OCCURRED
                );
                return Result.FAILURE;
            }
        }
    }

}
