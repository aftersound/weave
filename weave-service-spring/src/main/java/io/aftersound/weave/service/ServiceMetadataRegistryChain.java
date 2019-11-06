package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.Collection;

public class ServiceMetadataRegistryChain implements ServiceMetadataRegistry {

    private final ServiceMetadataRegistry[] serviceMetadataRegistries;

    public ServiceMetadataRegistryChain(ServiceMetadataRegistry[] serviceMetadataRegistries) {
        this.serviceMetadataRegistries = serviceMetadataRegistries;
    }

    @Override
    public ServiceMetadata getServiceMetadata(String id) {
        for (ServiceMetadataRegistry registry : serviceMetadataRegistries) {
            ServiceMetadata serviceMetadata = registry.getServiceMetadata(id);
            if (serviceMetadata != null) {
                return serviceMetadata;
            }
        }
        return null;
    }

    @Override
    public Collection<ServiceMetadata> all() {
        throw new UnsupportedOperationException();
    }
}
