package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the lifecycle of {@link JobSpec} (s) and also works as a registry
 * which allows access to {@link JobSpec} (s).
 */
class WeaveJobSpecManager implements JobSpecRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveJobSpecManager.class);

    protected volatile Map<String, JobSpec> jobSpecById = new HashMap<>();

    private final ExecutorService jobSpecPollThread = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final ObjectMapper jobSpecReader;
    private final Path metadataDirectory;

    public WeaveJobSpecManager(ObjectMapper jobSpecReader, Path metadataDirectory) {
        this.jobSpecReader = jobSpecReader;
        this.metadataDirectory = metadataDirectory;
    }

    @Override
    public JobSpec getJobSpec(String id) {
        return jobSpecById.get(id);
    }

    @Override
    public Collection<JobSpec> all() {
        return jobSpecById.values();
    }

    public void init() {
        if (!shutdown.get()) {
            // Job spec initial load
            jobSpecById = new JobSpecLoader(
                    jobSpecReader,
                    metadataDirectory
            ).load();

            jobSpecPollThread.submit(this::refreshMetadataInLoop);
        }
    }

    /**
     * Stop job spec polling thread
     */
    public void shutdown() {
        shutdown.compareAndSet(false, true);
    }

    /**
     * Periodically, fixed at every 5 seconds, load {@link JobSpec} from metadata directory
     */
    private void refreshMetadataInLoop() {
        try {
            while (!shutdown.get()) {
                try {
                    jobSpecById = new JobSpecLoader(
                            jobSpecReader,
                            metadataDirectory
                    ).load();

                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    LOGGER.warn("{}: job spec poll thread is interrupted", this, e); // not expected
                    break;
                }
            }
            LOGGER.info("{}: Returning from job spec pooling", this);
        } catch (Exception e) { // mostly an unrecoverable.
            LOGGER.error("{}: Exception while reading job spec from directory", this, e);
            throw e;
        }
    }

}
