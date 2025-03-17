package io.aftersound.service;

import io.aftersound.service.metadata.ServiceMetadata;

import java.util.Map;

/**
 * Conceptual entity which holds registered {@link ServiceMetadata}
 */
public interface ServiceMetadataRegistry {

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
     * @return the source spec
     */
    <SPEC> SPEC getSpec(SpecExtractor<SPEC> specExtractor);
}
