package io.aftersound.weave.resource;

public class NullResourceManager implements ResourceManager {

    @Override
    public ResourceDeclaration getDeclaration() {

        return new ResourceDeclaration() {
            @Override
            public ResourceType<?>[] getRequiredResourceTypes() {
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

}
