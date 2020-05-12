package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.JobSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This {@link JobSpecLoader} loads {@link JobSpec} (s) from json files under
 * metadata directory when commanded.
 */
public class JobSpecLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSpecLoader.class);

    private final ObjectMapper jobSpecReader;
    private final Path metadataDirectory;

    public JobSpecLoader(ObjectMapper jobSpecReader, Path metadataDirectory) {
        this.jobSpecReader = jobSpecReader;
        this.metadataDirectory = metadataDirectory;
    }

    /**
     * Load {@link JobSpec} (s) from json files under given metadata directory
     * @return
     *          a map of type and JobSpec
     */
    public Map<String, JobSpec> load() {

        final Map<String, JobSpec> jobSpecById = new HashMap<>();

        try (Stream<Path> paths = Files.list(metadataDirectory)){
            paths.forEach(path -> {
                if (!Files.isRegularFile(path)) {
                    return;
                }
                String fileName = path.getFileName().toString().toLowerCase();
                if (!(fileName.startsWith("job-") && fileName.endsWith(".json"))) {
                    return;
                }

                try {
                    JobSpec jobSpec = jobSpecReader.readValue(path.toFile(), JobSpec.class);
                    jobSpecById.put(jobSpec.getId(), jobSpec);
                } catch (IOException e) {
                    LOGGER.error("{}: Exception while reading JobSpec from file " + fileName, this, e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return jobSpecById;
    }
}
