package io.aftersound.weave.service.runtime;

import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.glassfish.jersey.uri.PathTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the life cycle of {@link ServiceMetadata} (s) and also works as
 * a registry which provides access to {@link ServiceMetadata}.
 *   When a {@link ServiceMetadata} is loaded, a service is created/realized.
 *   When a {@link ServiceMetadata} is updated, existing service's behavior changed
 *   When a {@link ServiceMetadata} is deleted, existing service is deleted.
 */
final class ServiceMetadataManager extends WithConfigAutoRefreshMechanism implements ServiceMetadataRegistry, Manageable<ServiceMetadata> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetadataManager.class);

    private static final boolean TOLERATE_EXCEPTION = true;

    private final String name;
    private final ConfigProvider<ServiceMetadata> serviceMetadataProvider;
    private final CacheRegistry cacheRegistry;

    private volatile Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new HashMap<>();
    private volatile Map<String, ServiceMetadata> serviceMetadataByPath = new HashMap<>();

    public ServiceMetadataManager(
            String name,
            ConfigProvider<ServiceMetadata> serviceMetadataProvider,
            ConfigUpdateStrategy configUpdateStrategy,
            CacheRegistry cacheRegistry) {
        super(configUpdateStrategy);

        this.name = name;
        this.serviceMetadataProvider = serviceMetadataProvider;
        this.cacheRegistry = cacheRegistry;
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
    public final ServiceMetadata matchServiceMetadata(String method, String requestPath, Map<String, String> extractedPathVariables) {
        for (Map.Entry<PathTemplate, ServiceMetadata> e : serviceMetadataByPathTemplate.entrySet()) {
            PathTemplate pathTemplate = e.getKey();
            ServiceMetadata serviceMetadata = e.getValue();
            if (serviceMetadata.getMethods().contains(method) && pathTemplate.match(requestPath, extractedPathVariables)) {
                return e.getValue();
            }
        }
        return null;
    }

    /**
     * @return all {@link ServiceMetadata} (s) currently managed by this {@link ServiceMetadataManager}
     */
    @Override
    public final Collection<ServiceMetadata> all() {
        return serviceMetadataByPathTemplate.values();
    }

    @Override
    public ManagementFacade<ServiceMetadata> getManagementFacade() {
        return new ManagementFacade<ServiceMetadata>() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<ServiceMetadata> entityType() {
                return ServiceMetadata.class;
            }

            @Override
            public void refresh() {
                loadConfigs(TOLERATE_EXCEPTION);
            }

            @Override
            public List<ServiceMetadata> list() {
                return new ArrayList<>(serviceMetadataByPathTemplate.values());
            }

            @Override
            public ServiceMetadata get(String id) {
                return serviceMetadataByPath.get(id);
            }

        };
    }

    /**
     * Load service metadata from given provider
     *
     * @param tolerateException whether to tolerate the exception occurred during loading configs
     */
    @Override
    synchronized void loadConfigs(boolean tolerateException) {
        // load service metadata from provider
        List<ServiceMetadata> serviceMetadataList = Collections.emptyList();
        try {
            serviceMetadataList = serviceMetadataProvider.getConfigList();
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading service metadata list from provider", e);
            if (tolerateException) {
                return;
            } else {
                throwException(e);
            }
        }

        Map<String, ServiceMetadata> serviceMetadataByPath = serviceMetadataList
                .stream()
                .collect(Collectors.toMap(ServiceMetadata::getPath, serviceMetadata -> serviceMetadata ));

        Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new LinkedHashMap<>();
        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
            String path = entry.getKey();
            ServiceMetadata serviceMetadata = entry.getValue();

            // initialize cache if necessary
            CacheControl cacheControl = serviceMetadata.getCacheControl();
            if (cacheControl != null && cacheRegistry != null && cacheRegistry.getCache(path) == null) {
                try {
                    cacheRegistry.initializeCache(path, cacheControl);
                } catch (Exception e) {
                    LOGGER.error("Exception occurred on creating cache for service {}:\n{}", path, e);
                    if (!tolerateException) {
                        throwException(e);
                    }
                }
            }

            // create map of PathTemplate and ServiceMetadata
            serviceMetadataByPathTemplate.put(new PathTemplate(path), serviceMetadata);
        }

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
        Map<String, ServiceMetadata> removed = figureOutRemoved(serviceMetadataByPath);
        for (Map.Entry<String, ServiceMetadata> entry : removed.entrySet()) {
            if (cacheRegistry != null) {
                cacheRegistry.unregisterAndDestroyCache(entry.getKey());
            }
        }

        // set to activate service metadata
        this.serviceMetadataByPathTemplate = serviceMetadataByPathTemplate;
        this.serviceMetadataByPath = serviceMetadataByPath;
    }

    private void throwException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }

    private Map<String, ServiceMetadata> figureOutRemoved(Map<String, ServiceMetadata> latest) {
        Set<String> retained = new HashSet<>(serviceMetadataByPath.keySet());
        retained.retainAll(latest.keySet());

        Set<String> removed = new HashSet<>(serviceMetadataByPath.keySet());
        removed.removeAll(retained);

        Map<String, ServiceMetadata> result = new HashMap<>(removed.size());
        for (String path : removed) {
            result.put(path, serviceMetadataByPath.get(path));
        }

        return result;
    }

}
