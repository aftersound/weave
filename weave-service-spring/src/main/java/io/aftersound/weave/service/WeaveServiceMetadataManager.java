package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the lifecycle of {@link ServiceMetadata } (s).
 * When a {@link ServiceMetadata} is loaded, a service is created/realized.
 * When a {@link ServiceMetadata} is updated, existing service's behavior changed
 * When a {@link ServiceMetadata} is deleted, existing service is deleted.
 */
class WeaveServiceMetadataManager extends ServiceMetadataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveServiceMetadataManager.class);

    private final ExecutorService serviceMetadataPollThread = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final ObjectMapper serviceMetadataReader;
    private final Path metadataDirectory;
    private final SecurityControlRegistry securityControlRegistry;

    public WeaveServiceMetadataManager(
            ObjectMapper serviceMetadataReader,
            Path metadataDirectory,
            SecurityControlRegistry securityControlRegistry) {
        this.serviceMetadataReader = serviceMetadataReader;
        this.metadataDirectory = metadataDirectory;
        this.securityControlRegistry = securityControlRegistry;
    }

    @Override
    public void init() {
        if (!shutdown.get()) {
            // service metadata initial load
            serviceMetadataById = new ServiceMetadataLoader(
                    serviceMetadataReader,
                    metadataDirectory
            ).load();

            // kick off the service metadata polling thread
            serviceMetadataPollThread.submit(this::refreshMetadataInLoop);
        }
    }

    /**
     * Shut down and clean up
     */
    public void shutdown() {
        // Stop service metadata polling thread
        shutdown.compareAndSet(false, true);
    }

    /**
     * Periodically, fixed at every 5 seconds, load {@link ServiceMetadata} from metadata directory
     */
    private void refreshMetadataInLoop() {
        try {
            while (!shutdown.get()) {
                try {
                    serviceMetadataById = new ServiceMetadataLoader(
                            serviceMetadataReader,
                            metadataDirectory
                    ).load();

                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    LOGGER.warn("{}: service metadata poll thread is interrupted", this, e); // not expected
                    break;
                }
            }
            LOGGER.info("{} returning from service metadata pooling", this);
        } catch (Exception e) { // mostly an unrecoverable.
            LOGGER.error("{} exception while reading service metadata from directory", e);
            throw e;
        }
    }

}
