package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.config.Config;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.service.resources.ResourceInitializer;
import io.aftersound.weave.service.resources.ResourceType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ResourcesInitializerImpl implements ResourceInitializer {

    private static final ResourcesInitializerImpl INSTANCE = new ResourcesInitializerImpl();

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private boolean initalized = false;

    private ResourcesInitializerImpl() {
    }

    static ResourcesInitializerImpl instance() {
        return INSTANCE;
    }

    @Override
    public ResourceType<?>[] getDependingResourceTypes() {
        return new ResourceType[] {
                new ResourceType(
                        DataClientRegistry.class.getName(),
                        DataClientRegistry.class
                ),
                new ResourceType(
                        ServiceMetadataRegistry.class.getName(),
                        ServiceMetadataRegistry.class
                ),
                new ResourceType(
                        JobSpecRegistry.class.getName(),
                        JobSpecRegistry.class
                )
        };
    }

    @Override
    public ResourceType<?>[] getShareableResourceTypes() {
        return new ResourceType[] {
                new ResourceType(
                        "service-library",
                        LibraryRegistry.class
                ),
                new ResourceType(
                        "service-extension",
                        LibraryRegistry.class
                ),
                new ResourceType(
                        "batch-library",
                        LibraryRegistry.class
                ),
                new ResourceType(
                        "batch-extension",
                        LibraryRegistry.class
                ),
                new ResourceType(
                        "stream-library",
                        LibraryRegistry.class
                ),
                new ResourceType(
                        "stream-extension",
                        LibraryRegistry.class
                )
        };
    }

    @Override
    public ResourceType<?>[] getResourceTypes() {
        return new ResourceType[0];
    }

    @Override
    public synchronized void initializeResources(
            ManagedResources managedResources,
            Config resourceConfig) throws Exception {

        if (!initalized) {
            managedResources.setResource(
                    "service-library",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/service/library")
            );

            managedResources.setResource(
                    "service-extension",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/service/extension")
            );

            managedResources.setResource(
                    "batch-library",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/batch/library")
            );

            managedResources.setResource(
                    "batch-extension",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/batch/extension")
            );

            managedResources.setResource(
                    "stream-library",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/stream/library")
            );

            managedResources.setResource(
                    "stream-extension",
                    createAndInitExtensionLibraryRegistry("${WEAVE_HOME}/stream/extension")
            );

            initalized = true;
        }
    }

    private LibraryRegistry createAndInitExtensionLibraryRegistry(
            String extensionDirectory) throws Exception {
        LibraryRegistry libraryRegistry = new LibraryRegistry();

        Path jsonFilePath = PathHandle.of(extensionDirectory + "/_libraries.json").path();
        if (Files.exists(jsonFilePath)) {
            List<LibraryInfo> libraries = MAPPER.readValue(
                    jsonFilePath.toFile(),
                    new TypeReference<List<LibraryInfo>>() {
                    }
            );
            for (LibraryInfo library : libraries) {
                String groupId = library.getDescription().get("groupId");
                String artifactId = library.getDescription().get("artifactId");
                String version = library.getDescription().get("version");
                libraryRegistry.addLibrary(groupId, artifactId, version, library);
            }
        }
        return libraryRegistry;
    }
}
