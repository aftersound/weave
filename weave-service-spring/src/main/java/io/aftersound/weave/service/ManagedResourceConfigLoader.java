package io.aftersound.weave.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.resource.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        Path path = metadataDirectory.resolve("resource-configs.json");
        try {
            ResourceConfig[] resourceConfigs = resourceConfigReader.readValue(path.toFile(), ResourceConfig[].class);
            if (resourceConfigs != null) {
                return Arrays.asList(resourceConfigs);
            } else {
                return Collections.emptyList();
            }
        } catch (IOException e) {
            LOGGER.error("Exception on reading List<ResourceConfig> from file {}", path.toString(), e);
            return Collections.emptyList();
        }
    }

}
