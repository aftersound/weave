package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.glassfish.jersey.uri.PathTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Conceptual entity which manges life cycle of {@link ServiceMetadata} (s)
 * and also works as a registry which provides access to {@link ServiceMetadata}.
 */
abstract class ServiceMetadataManager implements ServiceMetadataRegistry {

    protected volatile Map<PathTemplate, ServiceMetadata> serviceMetadataByPathTemplate = new HashMap<>();

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
     * do necessary initialization work
     */
    public abstract void init();

}
