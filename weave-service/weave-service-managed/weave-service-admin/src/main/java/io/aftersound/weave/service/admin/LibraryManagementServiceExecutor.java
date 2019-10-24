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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LibraryManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = LibraryManagementExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryManagementServiceExecutor.class);
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperBuilder.forJson().build();

    public LibraryManagementServiceExecutor(ManagedResources managedResources) {
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

        LibraryManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
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
        if (!(serviceMetadata.getExecutionControl() instanceof LibraryManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);

        LibraryManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();

        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );
        if (libraryRegistry == null) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_LIBRARY_REGISTRY_NOT_INITIALIZED.error(scope)
            );
            return false;
        }

        if (executionControl.getLibraryDirectory() == null) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_SERVICE_METADATA_INFO_UNSPECIFIED.error("ServiceMetadata.executionControl.libraryDirectory")
            );
            return false;
        }

        Path dir = PathHandle.of(executionControl.getLibraryDirectory()).path();
        if (!Files.exists(dir)) {
            context.getMessages().add(
                    Messages.TEMPLATE_DIRECTORY_NOT_EXISTS.error(executionControl.getLibraryDirectory())
            );
            return false;
        }

        return true;
    }

    private Object install(
            LibraryManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        String repository = request.firstWithName("repository").singleValue(String.class);
        String groupId = request.firstWithName("groupId").singleValue(String.class);
        String artifactId = request.firstWithName("artifactId").singleValue(String.class);
        String version = request.firstWithName("version").singleValue(String.class);

        String installationDirectory = PathHandle.of(executionControl.getLibraryDirectory()).path().toString();

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

                // copy library to target directory
                Path libraryPath = copyLibraryToTargetDirectory(librarySourceLocalPath, installationDirectory);

                // add library into into registry
                libraryRegistry.addLibrary(
                        groupId,
                        artifactId,
                        version,
                        createLibraryInfo(libraryPath, groupId, artifactId, version)
                );

                // write library registry into file for instance restart
                writeLibraryRegistryToFile(installationDirectory, libraryRegistry);

                return Result.SUCCESS;
            } catch (Exception e) {
                LOGGER.error("failed to install library", e);

                context.getMessages().addMessage(
                    Messages.INSTALL_LIBRARY_EXCEPTION_OCCURRED
                );
                return Result.FAILURE;
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
            String version) {
        LibraryInfo library = new LibraryInfo();
        library.setPath(libraryPath.getFileName().toString());

        Map<String, String> description = new HashMap<>();
        description.put("groupId", groupId);
        description.put("artifactId", artifactId);
        description.put("version", version);
        library.setDescription(description);

        return library;
    }

    private static void writeLibraryRegistryToFile(
            String targetDirectory,
            LibraryRegistry libraryRegistry) throws Exception {
        Path jsonFilePath = Paths.get(targetDirectory, "_libraries.json");
        File file = new File(jsonFilePath.toString());
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(file, libraryRegistry.getLibraries());
    }

    private Object list(
            LibraryManagementExecutionControl executionControl,
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
            LibraryManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {

        String scope = request.firstWithName("scope").singleValue(String.class);
        String type = request.firstWithName("type").singleValue(String.class);
        String groupId = request.firstWithName("groupId").singleValue(String.class);
        String artifactId = request.firstWithName("artifactId").singleValue(String.class);
        String version = request.firstWithName("version").singleValue(String.class);

        String targetDirectory = PathHandle.of(executionControl.getLibraryDirectory()).path().toString();

        LibraryRegistry libraryRegistry = managedResources.getResource(
                scope + "-" + type,
                LibraryRegistry.class
        );
        synchronized (libraryRegistry) {
            try {
                LibraryInfo library = libraryRegistry.getLibrary(groupId, artifactId, version);
                if (library == null) {
                    context.getMessages().addMessage(
                            Messages.UNINSTALL_LIBRARY_NOT_FOUND.asWarning()
                    );
                    return Result.FAILURE;
                }

                Path libraryPath = Paths.get(targetDirectory, library.getPath());
                if (Files.exists(libraryPath)) {
                    Files.delete(libraryPath);
                }

                // remove library info after library file is removed from target directory
                libraryRegistry.removeLibrary(groupId, artifactId, version);

                // write library registry into file for instance restart
                writeLibraryRegistryToFile(targetDirectory, libraryRegistry);

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
