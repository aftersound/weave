package io.aftersound.weave.service;

import io.aftersound.weave.file.PathHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class DirectoryBasedConfigProvider<CONFIG> extends ConfigProvider<CONFIG> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryBasedConfigProvider.class);

    private final String directory;
    private final Class<CONFIG> configClass;

    public DirectoryBasedConfigProvider(String directory, Class<CONFIG> configClass) {
        this.directory = directory;
        this.configClass = configClass;
    }

    @Override
    public List<CONFIG> getConfigList() {
        final List<CONFIG> configList = new ArrayList<>();

        Path configDirectory = PathHandle.of(directory).path();

        try (Stream<Path> paths = Files.list(configDirectory)) {
            paths.forEach(path -> {
                if (!Files.isRegularFile(path)) {
                    return;
                }
                String fileName = path.getFileName().toString().toLowerCase();
                if (!(fileName.endsWith(".json"))) {
                    return;
                }

                try {
                    CONFIG config = configReader.readValue(path.toFile(), configClass);
                    configList.add(config);
                } catch (IOException e) {
                    LOGGER.error("Exception while reading resource config from file {}", fileName, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return configList;
    }

}
