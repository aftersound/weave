package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * Conceptual entity which holds registered {@link ServiceMetadata}
 */
public interface ServiceMetadataRegistry {

    /**
     * Get the {@link ServiceMetadata} which has specified path template
     *
     * @param pathTemplate - path template
     * @return - {@link ServiceMetadata} which has specified path template
     */
    ServiceMetadata getServiceMetadata(String pathTemplate);

    /**
     * Find and return the {@link ServiceMetadata} which has path template
     * which matches given request path
     *
     * @param method                 - HTTP method
     * @param requestPath            - service request path
     * @param extractedPathVariables - a container which could hold path parameters as side return
     * @return the matching {@link ServiceMetadata} if found
     */
    ServiceMetadata matchServiceMetadata(String method, String requestPath, Map<String, String> extractedPathVariables);

    /**
     * @return all {@link ServiceMetadata} in this registry
     */
    Collection<ServiceMetadata> all();
}
