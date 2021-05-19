package io.aftersound.weave.service.security;

import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.HashMap;

public class AuthControlRegistry {

    private final ServiceMetadataRegistry serviceMetadataRegistry;

    public AuthControlRegistry(ServiceMetadataRegistry serviceMetadataRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
    }

    public AuthControl getAuthControl(String method, String requestPath) {
        ServiceMetadata serviceMetadata = serviceMetadataRegistry.matchServiceMetadata(
                method,
                requestPath,
                new HashMap<>()
        );
        if (serviceMetadata != null) {
            return serviceMetadata.getAuthControl();
        }
        return null;
    }

}
