package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This {@link ServiceMetadataLoader} loads {@link ServiceMetadata} (s) from json files under
 * metadata directory when commanded.
 */
class ServiceMetadataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataLoader.class);

    private final ObjectMapper serviceMetadataReader;
    private final Path metadataDirectory;

    public ServiceMetadataLoader(
            ObjectMapper serviceMetadataReader,
            Path metadataDirectory) {
        this.serviceMetadataReader = serviceMetadataReader;
        this.metadataDirectory = metadataDirectory;
    }

    /**
     * Load {@link ServiceMetadata} (s) from json files under given metadata directory
     * @return
     *          a map of type and ServiceMetadata
     */
    public Map<String, ServiceMetadata> load() {

        final Map<String, ServiceMetadata> serviceMetadataByPath = new HashMap<>();

        try (Stream<Path> paths = Files.list(metadataDirectory)) {
            paths.forEach(path -> {
                if (!Files.isRegularFile(path)) {
                    return;
                }
                String fileName = path.getFileName().toString().toLowerCase();
                if (!(fileName.startsWith("service-") && fileName.endsWith(".json"))) {
                    return;
                }

                try {
                    ServiceMetadata serviceMetdata = serviceMetadataReader.readValue(path.toFile(), ServiceMetadata.class);
                    serviceMetadataByPath.put(serviceMetdata.getPath(), serviceMetdata);
                } catch (IOException e) {
                    LOGGER.error("{}: Exception while reading ServiceMetadata from file " + fileName, this, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return serviceMetadataByPath;
    }

}
