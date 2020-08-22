package io.aftersound.weave.resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleResourceManager implements ResourceManager {

    private final List<ResourceType<?>> dependingResourceTypes;

    private SimpleResourceManager(List<ResourceType<?>> dependingResourceTypes) {
        this.dependingResourceTypes = dependingResourceTypes;
    }

    public static ResourceManager withDependingResourceTypes(ResourceType<?>... dependingResourceTypes) {
        if (dependingResourceTypes != null) {
            return new SimpleResourceManager(Arrays.asList(dependingResourceTypes));
        } else {
            return new SimpleResourceManager(Collections.<ResourceType<?>>emptyList());
        }
    }

    @Override
    public ResourceDeclaration getDeclaration() {
        return new ResourceDeclaration() {
            @Override
            public ResourceType<?>[] getRequiredResourceTypes() {
                return dependingResourceTypes.toArray(new ResourceType[dependingResourceTypes.size()]);
            }

            @Override
            public ResourceType<?>[] getShareableResourceTypes() {
                return new ResourceType[0];
            }

            @Override
            public ResourceType<?>[] getResourceTypes() {
                return new ResourceType[0];
            }
        };
    }

    @Override
    public boolean accept(ResourceConfig resourceConfig) {
        return false;
    }

    @Override
    public void initializeResources(ManagedResources managedResources, ResourceConfig resourceConfig) throws Exception {
        // Do nothing
    }

    @Override
    public void destroyResources(ManagedResources managedResources, ResourceConfig resourceConfig) throws Exception {
        // Do nothing
    }

}
