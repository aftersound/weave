package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.config.Config;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.service.resources.ResourceInitializer;
import io.aftersound.weave.service.resources.ResourceType;

public class ResourceInitializerImpl implements ResourceInitializer {

    @Override
    public ResourceType<?>[] getDependingResourceTypes() {
        return new ResourceType[] {
                Constants.DATA_CLIENT_REGISTRY_RESOURCE_TYPE
        };
    }

    @Override
    public ResourceType<?>[] getShareableResourceTypes() {
        return new ResourceType[0];
    }

    @Override
    public ResourceType<?>[] getResourceTypes() {
        return new ResourceType[0];
    }

    @Override
    public void initializeResources(ManagedResources managedResources, Config resourceConfig) {
    }
}
