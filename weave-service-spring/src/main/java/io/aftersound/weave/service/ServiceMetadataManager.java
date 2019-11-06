package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Conceptual entity which manges life cycle of {@link ServiceMetadata} (s)
 * and also works as a registry which provides access to {@link ServiceMetadata}.
 */
abstract class ServiceMetadataManager implements ServiceMetadataRegistry {

    protected volatile Map<String, ServiceMetadata> serviceMetadataById = new HashMap<>();

    /**
     * Return {@link ServiceMetadata} with specified id, if exists
     * @param id
     *          identifier of target ServiceMetadata
     * @return
     *          a ServiceMetadata with specified id, if exists
     */
    @Override
    public final ServiceMetadata getServiceMetadata(String id) {
        return serviceMetadataById.get(id);
    }

    /**
     * @return
     *          all {@link ServiceMetadata} (s) currently managed by this {@link ServiceMetadataManager}
     */
    @Override
    public final Collection<ServiceMetadata> all() {
        return serviceMetadataById.values();
    }

    /**
     * do necessary initialization work
     */
    public abstract void init();

}
