package io.aftersound.weave.service;

import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.resources.ResourceType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ManagedResourcesImpl implements ManagedResources {

    private Map<String, Object> resourceByName = new HashMap<>();

    @Override
    public void setResource(String name, Object resource) {
        resourceByName.put(name, resource);
    }

    @Override
    public <R> R getResource(ResourceType<R> resourceType) {
        return getResource(resourceType.name(), resourceType.type());
    }

    @Override
    public <R> R getResource(String name, Class<R> resourceType) {
        Object resource = resourceByName.get(name);
        if (resourceType.isInstance(resource)) {
            return resourceType.cast(resource);
        }
        return null;
    }

    @Override
    public Collection<String> resourceNames() {
        return resourceByName.keySet();
    }

}
