package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.resource.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class ManagedResourceConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedResourceConfigLoader.class);

    private final ObjectMapper resourceConfigReader;
    private final Path metadataDirectory;

    public ManagedResourceConfigLoader(
            ObjectMapper resourceConfigReader,
            Path metadataDirectory) {
        this.resourceConfigReader = resourceConfigReader;
        this.metadataDirectory = metadataDirectory;
    }

    /**
     * Load {@link ResourceConfig} (s) from json files under given metadata directory
     * @return
     *          a list of {@link ResourceConfig}
     */
    public List<ResourceConfig> load() {
        final List<ResourceConfig> resourceConfigList = new ArrayList<>();

        try (Stream<Path> paths = Files.list(metadataDirectory)) {
            paths.forEach(path -> {
                if (!Files.isRegularFile(path)) {
                    return;
                }
                String fileName = path.getFileName().toString().toLowerCase();
                if (!(fileName.endsWith(".json"))) {
                    return;
                }

                try {
                    ResourceConfig resourceConfig = resourceConfigReader.readValue(path.toFile(), ResourceConfig.class);
                    resourceConfigList.add(resourceConfig);
                } catch (IOException e) {
                    LOGGER.error("Exception while reading resource config from file {}", fileName, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resourceConfigList;
    }

}
