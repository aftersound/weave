package io.aftersound.weave.service.rl;

import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.HashMap;

public class RateLimitControlRegistry {

    private final ServiceMetadataRegistry serviceMetadataRegistry;

    public RateLimitControlRegistry(ServiceMetadataRegistry serviceMetadataRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
    }

    public RateLimitControl getRateLimitControl(String method, String requestPath) {
        ServiceMetadata serviceMetadata = serviceMetadataRegistry.matchServiceMetadata(
                method,
                requestPath,
                new HashMap<>()
        );
        return serviceMetadata != null ? serviceMetadata.getRateLimitControl() : null;
    }

}
