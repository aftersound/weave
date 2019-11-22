package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
    private final CacheRegistry cacheRegistry;

    public WeaveServiceMetadataManager(
            ObjectMapper serviceMetadataReader,
            Path metadataDirectory,
            CacheRegistry cacheRegistry) {
        this.serviceMetadataReader = serviceMetadataReader;
        this.metadataDirectory = metadataDirectory;
        this.cacheRegistry = cacheRegistry;
    }

    @Override
    public void init() {
        if (!shutdown.get()) {
            loadAndInit();

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
                    loadAndInit();

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

    private void loadAndInit() {
        // service metadata initial load
        Map<String, ServiceMetadata> serviceMetadataById = new ServiceMetadataLoader(
                serviceMetadataReader,
                metadataDirectory
        ).load();

        // initialize cache if necessary
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataById.entrySet()) {
            String id = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();
            CacheControl cacheControl = serviceMetadata.getCacheControl();

            if (cacheControl != null && cacheRegistry.getCache(id) == null) {
                try {
                    cacheRegistry.initializeCache(id, cacheControl);
                } catch (Exception e) {
                    LOGGER.error("{} occurred when attempting to create cache for service {}", e, id);
                }
            }
        }

        // set to activate service metadata
        this.serviceMetadataById = serviceMetadataById;

        // destroy cache if necessary
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataById.entrySet()) {
            String id = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();
            CacheControl cacheControl = serviceMetadata.getCacheControl();

            if (cacheControl == null) {
                cacheRegistry.unregisterAndDestroyCache(id);
            }
        }

        // identify removed and destroy associated cache if any
        Map<String, ServiceMetadata> removed = figureOutRemoved(this.serviceMetadataById, serviceMetadataById);
        for (Map.Entry<String, ServiceMetadata> entry : removed.entrySet()) {
            String id = entry.getKey();
            cacheRegistry.unregisterAndDestroyCache(id);
        }

        this.serviceMetadataById = serviceMetadataById;
    }

    private Map<String, ServiceMetadata> figureOutRemoved(
            Map<String, ServiceMetadata> existing,
            Map<String, ServiceMetadata> latest) {
        Set<String> retained = new HashSet<>(existing.keySet());
        retained.retainAll(latest.keySet());

        Set<String> removed = new HashSet<>(existing.keySet());
        removed.removeAll(retained);

        Map<String, ServiceMetadata> result = new HashMap<>(removed.size());
        for (String id : removed) {
            result.put(id, existing.get(id));
        }
        return result;
    }

}
