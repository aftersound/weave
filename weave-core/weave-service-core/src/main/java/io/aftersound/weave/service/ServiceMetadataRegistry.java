package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.Collection;

/**
 * Conceptual entity which holds registered {@link ServiceMetadata}
 */
public interface ServiceMetadataRegistry {
    ServiceMetadata getServiceMetadata(String id);
    Collection<ServiceMetadata> all();
}
