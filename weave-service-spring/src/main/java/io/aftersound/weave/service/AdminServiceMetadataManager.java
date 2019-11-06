package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.security.SecurityControlRegistry;

import java.nio.file.Path;
import java.util.Map;

/**
 * This {@link AdminServiceMetadataManager} manages the lifecycle of
 * {@link ServiceMetadata} (s) related to administration functionalities.
 */
class AdminServiceMetadataManager extends ServiceMetadataManager {

    private final ObjectMapper serviceMetadataReader;
    private final Path metadataDirectory;
    private final SecurityControlRegistry securityControlRegistry;

    public AdminServiceMetadataManager(
            ObjectMapper serviceMetadataReader,
            Path metadataDirectory,
            SecurityControlRegistry securityControlRegistry) {
        this.serviceMetadataReader = serviceMetadataReader;
        this.metadataDirectory = metadataDirectory;
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public void init() {
        // load administration related ServiceMetadata once and only once
        Map<String, ServiceMetadata> serviceMetadataMap = new ServiceMetadataLoader(
                serviceMetadataReader,
                metadataDirectory
        ).load();

        serviceMetadataById.putAll(serviceMetadataMap);
    }
}
