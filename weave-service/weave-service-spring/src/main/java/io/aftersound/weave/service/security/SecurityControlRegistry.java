package io.aftersound.weave.service.security;

import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.HashMap;

public class SecurityControlRegistry {

    private final ServiceMetadataRegistry serviceMetadataRegistry;

    public SecurityControlRegistry(ServiceMetadataRegistry serviceMetadataRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
    }

    private SecurityControl getSecurityControl(String requestPath) {
        ServiceMetadata serviceMetadata = serviceMetadataRegistry.matchServiceMetadata(requestPath, new HashMap<>());
        if (serviceMetadata != null) {
            return serviceMetadata.getSecurityControl();
        }
        return null;
    }

    public AuthenticationControl getAuthenticationControl(String requestPath) {
        SecurityControl securityControl = getSecurityControl(requestPath);
        if (securityControl == null) {
            return null;
        }
        return securityControl.getAuthenticationControl();
    }

    public AuthorizationControl getAuthorizationControl(String requestPath) {
        SecurityControl securityControl = getSecurityControl(requestPath);
        if (securityControl == null) {
            return null;
        }
        return securityControl.getAuthorizationControl();
    }

}
