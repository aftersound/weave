package io.aftersound.weave.service.runtime;

import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.Collection;
import java.util.Map;

public class ServiceMetadataRegistryChain implements ServiceMetadataRegistry {

    private final ServiceMetadataRegistry[] serviceMetadataRegistries;

    public ServiceMetadataRegistryChain(ServiceMetadataRegistry[] serviceMetadataRegistries) {
        this.serviceMetadataRegistries = serviceMetadataRegistries;
    }

    @Override
    public ServiceMetadata getServiceMetadata(String path) {
        for (ServiceMetadataRegistry registry : serviceMetadataRegistries) {
            ServiceMetadata serviceMetadata = registry.getServiceMetadata(path);
            if (serviceMetadata != null) {
                return serviceMetadata;
            }
        }
        return null;
    }

    @Override
    public ServiceMetadata matchServiceMetadata(String requestPath, Map<String, String> extractedPathVariables) {
        for (ServiceMetadataRegistry registry : serviceMetadataRegistries) {
            ServiceMetadata serviceMetadata = registry.matchServiceMetadata(requestPath, extractedPathVariables);
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
