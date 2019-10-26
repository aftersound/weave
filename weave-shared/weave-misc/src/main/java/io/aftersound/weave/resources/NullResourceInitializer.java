package io.aftersound.weave.resources;

import io.aftersound.weave.config.Config;

public class NullResourceInitializer implements ResourceInitializer {

    @Override
    public ResourceType<?>[] getDependingResourceTypes() {
        return new ResourceType[0];
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
