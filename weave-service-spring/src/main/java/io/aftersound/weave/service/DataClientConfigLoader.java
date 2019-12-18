package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.dataclient.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This {@link DataClientConfigLoader} loads {@link Endpoint} (s) from json files under
 * metadata directory when commanded.
 */
class DataClientConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClientConfigLoader.class);

    private final ObjectMapper dataClientConfigReader;
    private final Path metadataDirectory;

    public DataClientConfigLoader(
            ObjectMapper dataClientConfigReader,
            Path metadataDirectory) {
        this.dataClientConfigReader = dataClientConfigReader;
        this.metadataDirectory = metadataDirectory;
    }

    /**
     * Load {@link Endpoint} (s) from json files under given metadata directory
     * @return
     *          a map of type and Endpoint
     */
    public Map<String, Endpoint> load() {
        final Map<String, Endpoint> endpointById = new LinkedHashMap<>();

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
                    Endpoint endpoint = dataClientConfigReader.readValue(path.toFile(), Endpoint.class);
                    endpointById.put(endpoint.getId(), endpoint);
                } catch (IOException e) {
                    LOGGER.error("Exception while reading data client config from file {}", fileName, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return endpointById;
    }
}
