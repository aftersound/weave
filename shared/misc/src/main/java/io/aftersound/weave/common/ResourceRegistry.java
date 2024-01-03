package io.aftersound.weave.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resource registry which holds resources to be referenced by identifier
 */
public final class ResourceRegistry {

    public static final String DEFAULT_ID = "ResourceRegistry";

    private final Map<String, Object> byId = new ConcurrentHashMap<>();

    /**
     * Register the resource with specified identifier
     *
     * @param id       resource identifier
     * @param resource resource object
     * @param <T>      type of resource in generic form
     */
    public <T> ResourceRegistry register(String id, T resource) {
        byId.putIfAbsent(id, resource);
        return this;
    }

    /**
     * Get resource of expected type with given identifier
     *
     * @param id  resource identifier
     * @param <T> expected type in generic form
     * @return the resource with given identifier if exists
     */
    public <T> T get(String id) {
        return (T) (id != null ? byId.get(id) : null);
    }

}

