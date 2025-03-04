package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.func.FuncFactory;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.SpecExtractor;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.ParamField;
import org.glassfish.jersey.uri.PathTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private final ConfigProvider configProvider;
    private final ObjectMapper configReader;
    private final CacheRegistry cacheRegistry;
    private final FuncFactory funcFactory;

    private volatile ConfigHolder configHolder = null;
    private volatile List<ServiceMetadata> serviceMetadataList = Collections.emptyList();
    private volatile Map<PathTemplate, Map<String, ServiceMetadata>> lookup = Collections.emptyMap();

    public ServiceMetadataManager(
            String name,
            ConfigProvider configProvider,
            ObjectMapper configReader,
            ConfigUpdateStrategy configUpdateStrategy,
            CacheRegistry cacheRegistry,
            FuncFactory funcFactory) {
        super(configUpdateStrategy);

        this.name = name;
        this.configProvider = configProvider;
        this.configReader = configReader;
        this.cacheRegistry = cacheRegistry;
        this.funcFactory = funcFactory;
    }

    @Override
    public ServiceMetadata matchServiceMetadata(String method, String requestPath, Map<String, String> extractedPathVariables) {
        for (Map.Entry<PathTemplate, Map<String, ServiceMetadata>> e : lookup.entrySet()) {
            PathTemplate pathTemplate = e.getKey();
            Map<String, ServiceMetadata> serviceMetadataByMethod = e.getValue();
            if (serviceMetadataByMethod.containsKey(method) && pathTemplate.match(requestPath, extractedPathVariables)) {
                return serviceMetadataByMethod.get(method);
            }
        }
        return null;
    }

    @Override
    public <SPEC> SPEC getSpec(SpecExtractor<SPEC> specExtractor) {
        if (specExtractor instanceof ExtensionAware) {
            ((ExtensionAware) specExtractor).setObjectMapper(configReader);
        }
        return specExtractor.extract(configHolder != null ? configHolder.config() : null);
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
                return serviceMetadataList;
            }

            @Override
            public ServiceMetadata get(String id) {
                String method;
                String path;

                int index = id.indexOf(':');
                if (index >= 0) {
                    method = id.substring(0, index);
                    path = id.substring(index + 1);
                } else {
                    method = "GET";
                    path = id;
                }

                return matchServiceMetadata(method, path, new HashMap<>());
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
        ConfigHolder configHolder = null;
        List<ServiceMetadata> serviceMetadataList = Collections.emptyList();
        try {
            configHolder = configProvider.getConfig();
            serviceMetadataList = configHolder.getServiceMetadataList(configReader);
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading service metadata list from provider", e);
            if (tolerateException) {
                return;
            } else {
                throwException(e);
            }
        }

        Map<PathTemplate, Map<String, ServiceMetadata>> lookup = new HashMap<>();
        for (ServiceMetadata sm : serviceMetadataList) {
            PathTemplate pathTemplate = new PathTemplate(sm.getPath());

            if (!lookup.containsKey(pathTemplate)) {
                lookup.put(pathTemplate, new HashMap<>());
            }

            Set<String> methods = new HashSet<>();
            for (String method : sm.getMethods()) {
                if (!methods.contains(method)) {
                    methods.add(method);
                    lookup.get(pathTemplate).put(method, sm);
                } else {
                    final String msg = String.format("More than 1 ServiceMetadata for '%s: %s'", method, sm.getPath());
                    LOGGER.error(msg);
                    if (!tolerateException) {
                        throwException(new RuntimeException(msg));
                    }
                }
            }

            for (ParamField pf :  sm.getParamFields()) {
                pf.initDirectives(funcFactory);
            }

//            // initialize cache if necessary
//            CacheControl cacheControl = sm.getCacheControl();
//            if (cacheControl != null && cacheRegistry != null) {
//                try {
//                    cacheRegistry.initializeCache(path, cacheControl);
//                } catch (Exception e) {
//                    LOGGER.error("Exception occurred on creating cache for service {}:\n{}", path, e);
//                    if (!tolerateException) {
//                        throwException(e);
//                    }
//                }
//            }
        }

//        Map<String, ServiceMetadata> serviceMetadataByPath = serviceMetadataList
//                .stream()
//                .collect(Collectors.toMap(ServiceMetadata::getPath, serviceMetadata -> serviceMetadata ));
//
//        Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new LinkedHashMap<>();
//        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
//            String path = entry.getKey();
//            ServiceMetadata serviceMetadata = entry.getValue();
//
//            // initialize cache if necessary
//            CacheControl cacheControl = serviceMetadata.getCacheControl();
//            if (cacheControl != null && cacheRegistry != null && cacheRegistry.getCache(path) == null) {
//                try {
//                    cacheRegistry.initializeCache(path, cacheControl);
//                } catch (Exception e) {
//                    LOGGER.error("Exception occurred on creating cache for service {}:\n{}", path, e);
//                    if (!tolerateException) {
//                        throwException(e);
//                    }
//                }
//            }
//
//            // create map of PathTemplate and ServiceMetadata
//            serviceMetadataByPathTemplate.put(new PathTemplate(path), serviceMetadata);
//        }
//
//        // destroy cache if necessary
//        for (Map.Entry<String, ServiceMetadata> entry : serviceMetadataByPath.entrySet()) {
//            String path = entry.getKey();
//            ServiceMetadata serviceMetadata = entry.getValue();
//            CacheControl cacheControl = serviceMetadata.getCacheControl();
//
//            if (cacheControl == null && cacheRegistry != null) {
//                cacheRegistry.unregisterAndDestroyCache(path);
//            }
//        }
//
//        // identify removed and destroy associated cache if any
//        Map<String, ServiceMetadata> removed = figureOutRemoved(serviceMetadataByPath);
//        for (Map.Entry<String, ServiceMetadata> entry : removed.entrySet()) {
//            if (cacheRegistry != null) {
//                cacheRegistry.unregisterAndDestroyCache(entry.getKey());
//            }
//        }

        // set to activate service metadata
        this.lookup = lookup;
        this.configHolder = configHolder;
        this.serviceMetadataList = Collections.unmodifiableList(serviceMetadataList);
    }

    private void throwException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }

//    private Map<String, ServiceMetadata> figureOutRemoved(Map<String, ServiceMetadata> latest) {
//        Set<String> retained = new HashSet<>(serviceMetadataByPath.keySet());
//        retained.retainAll(latest.keySet());
//
//        Set<String> removed = new HashSet<>(serviceMetadataByPath.keySet());
//        removed.removeAll(retained);
//
//        Map<String, ServiceMetadata> result = new HashMap<>(removed.size());
//        for (String path : removed) {
//            result.put(path, serviceMetadataByPath.get(path));
//        }
//
//        return result;
//    }

}
