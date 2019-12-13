package io.aftersound.weave.resource;

public class NullResourceManager implements ResourceManager<ResourceConfig> {

    @Override
    public ResourceDeclaration getDeclaration() {

        return new ResourceDeclaration() {
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
        };

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
