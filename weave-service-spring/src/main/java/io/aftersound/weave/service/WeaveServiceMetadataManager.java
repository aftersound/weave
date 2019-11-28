package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.glassfish.jersey.uri.PathTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
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
        Map<String, ServiceMetadata> serviceMetadataByPath = new ServiceMetadataLoader(
                serviceMetadataReader,
                metadataDirectory
        ).load();

        Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new LinkedHashMap<>();
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
            String path = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();

            // create map of PathTemplate and ServiceMetadata
            serviceMetadataByPathTemplate.put(new PathTemplate(path), serviceMetadata);

            // initialize cache if necessary
            CacheControl cacheControl = serviceMetadata.getCacheControl();
            if (cacheControl != null && cacheRegistry.getCache(path) == null) {
                try {
                    cacheRegistry.initializeCache(path, cacheControl);
                } catch (Exception e) {
                    LOGGER.error("{} occurred when attempting to create cache for service {}", e, path);
                }
            }
        }

        // set to activate service metadata
        this.serviceMetadataByPathTemplate = serviceMetadataByPathTemplate;

        // destroy cache if necessary
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
            String path = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();
            CacheControl cacheControl = serviceMetadata.getCacheControl();

            if (cacheControl == null) {
                cacheRegistry.unregisterAndDestroyCache(path);
            }
        }

        // identify removed and destroy associated cache if any
        Map<String, ServiceMetadata> removed = figureOutRemoved(serviceMetadataByPathTemplate, serviceMetadataByPath);
        for (Map.Entry<String, ServiceMetadata> entry : removed.entrySet()) {
            cacheRegistry.unregisterAndDestroyCache(entry.getKey());
        }
    }

    private Map<String, ServiceMetadata> figureOutRemoved(
            Map<PathTemplate, ServiceMetadata> existing,
            Map<String, ServiceMetadata> latest) {
        Map<String, ServiceMetadata> existingByPath = new HashMap<>();
        for (Map.Entry<PathTemplate, ServiceMetadata> entry : existing.entrySet()) {
            existingByPath.put(entry.getKey().getTemplate(), entry.getValue());
        }

        Set<String> retained = new HashSet<>(existingByPath.keySet());
        retained.retainAll(latest.keySet());

        Set<String> removed = new HashSet<>(existingByPath.keySet());
        removed.removeAll(retained);

        Map<String, ServiceMetadata> result = new HashMap<>(removed.size());
        for (String path : removed) {
            result.put(path, existingByPath.get(path));
        }

        return result;
    }

}
