package io.aftersound.weave.service;

import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.glassfish.jersey.uri.PathTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Manages the life cycle of {@link ServiceMetadata} (s) and also works as
 * a registry which provides access to {@link ServiceMetadata}.
 *   When a {@link ServiceMetadata} is loaded, a service is created/realized.
 *   When a {@link ServiceMetadata} is updated, existing service's behavior changed
 *   When a {@link ServiceMetadata} is deleted, existing service is deleted.
 */
public final class ServiceMetadataManager implements ServiceMetadataRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataManager.class);

    private final ConfigProvider<ServiceMetadata> serviceMetadataProvider;
    private final CacheRegistry cacheRegistry;

    protected volatile Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new HashMap<>();

    private final ExecutorService serviceMetadataPollThread = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private long refreshInterval = 5000L;

    public ServiceMetadataManager(
            ConfigProvider<ServiceMetadata> serviceMetadataProvider,
            CacheRegistry cacheRegistry) {
        this.serviceMetadataProvider = serviceMetadataProvider;
        this.cacheRegistry = cacheRegistry;
    }

    public ServiceMetadataManager refreshInterval(long refreshInterval) {
        // minimum at 0.5 second
        if (refreshInterval >= 500L) {
            this.refreshInterval = refreshInterval;
        }
        return this;
    }

    @Override
    public final ServiceMetadata getServiceMetadata(String path) {
        for (Map.Entry<PathTemplate, ServiceMetadata> entry : serviceMetadataByPathTemplate.entrySet()) {
            if (entry.getKey().getTemplate().equals(path)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public final ServiceMetadata matchServiceMetadata(String requestPath, Map<String, String> extractedPathVariables) {
        for (Map.Entry<PathTemplate, ServiceMetadata> entry : serviceMetadataByPathTemplate.entrySet()) {
            PathTemplate pathTemplate = entry.getKey();
            if (pathTemplate.match(requestPath, extractedPathVariables)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * @return
     *          all {@link ServiceMetadata} (s) currently managed by this {@link ServiceMetadataManager}
     */
    @Override
    public final Collection<ServiceMetadata> all() {
        return serviceMetadataByPathTemplate.values();
    }

    /**
     * Initialize and set up
     */
    public void init() {
        if (!shutdown.get()) {
            loadMetadata();

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
     * Load service metadata from given provider
     */
    public void loadMetadata() {
        // service metadata initial load
        List<ServiceMetadata> serviceMetadataList = serviceMetadataProvider.getConfigList();
        Map<String, ServiceMetadata> serviceMetadataByPath = serviceMetadataList
                .stream()
                .collect(Collectors.toMap(ServiceMetadata::getPath, serviceMetadata -> serviceMetadata ));

        Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new LinkedHashMap<>();
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
            String path = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();

            // create map of PathTemplate and ServiceMetadata
            serviceMetadataByPathTemplate.put(new PathTemplate(path), serviceMetadata);

            // initialize cache if necessary
            CacheControl cacheControl = serviceMetadata.getCacheControl();
            if (cacheControl != null && cacheRegistry != null && cacheRegistry.getCache(path) == null) {
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

            if (cacheControl == null && cacheRegistry != null) {
                cacheRegistry.unregisterAndDestroyCache(path);
            }
        }

        // identify removed and destroy associated cache if any
        Map<String, ServiceMetadata> removed = figureOutRemoved(serviceMetadataByPathTemplate, serviceMetadataByPath);
        for (Map.Entry<String, ServiceMetadata> entry : removed.entrySet()) {
            if (cacheRegistry != null) {
                cacheRegistry.unregisterAndDestroyCache(entry.getKey());
            }
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

    /**
     * Periodically, fixed at every 5 seconds, load {@link ServiceMetadata} from metadata directory
     */
    private void refreshMetadataInLoop() {
        try {
            while (!shutdown.get()) {
                try {
                    loadMetadata();

                    Thread.sleep(refreshInterval);
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
