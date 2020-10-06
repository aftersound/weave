package io.aftersound.weave.resource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleResourceDeclaration implements ResourceDeclaration {

    private final List<ResourceType<?>> required;

    private SimpleResourceDeclaration(List<ResourceType<?>> required) {
        this.required = required;
    }

    public static ResourceDeclaration withRequired(ResourceType<?>... resourceTypes) {
        return new SimpleResourceDeclaration(
                resourceTypes != null ? Arrays.asList(resourceTypes) : Collections.<ResourceType<?>>emptyList()
        );
    }

    @Override
    public ResourceType<?>[] getRequiredResourceTypes() {
        return required.toArray(new ResourceType<?>[required.size()]);
    }
}
