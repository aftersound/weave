package io.aftersound.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resource registry which holds resources to be referenced by identifier
 */
@SuppressWarnings("unchecked")
public final class ResourceRegistry {

    private final Map<String, Object> byId = new ConcurrentHashMap<>();

    /**
     * Register the resource with specified identifier if absent
     *
     * @param id       - resource identifier
     * @param resource - resource object
     * @param <T>      - type of resource in generic form
     * @return this {@link ResourceRegistry}
     */
    public <T> ResourceRegistry registerIfAbsent(String id, T resource) {
        byId.putIfAbsent(id, resource);
        return this;
    }

    /**
     * Register the resource with specified identifier
     *
     * @param id       - resource identifier
     * @param resource - resource object
     * @param <T>      - type of resource in generic form
     * @return this {@link ResourceRegistry}
     */
    public <T> ResourceRegistry register(String id, T resource) {
        byId.put(id, resource);
        return this;
    }

    /**
     * Unregister resource with specified id
     *
     * @param id  - resource identifier
     * @param <T> - type of resource in generic form
     * @return this {@link ResourceRegistry}
     */
    public <T> T unregister(String id) {
        return (T) (id != null ? byId.remove(id) : null);
    }

    /**
     * Get resource of expected type with given identifier
     *
     * @param id  - resource identifier
     * @param <T> - expected type in generic form
     * @return the resource with given identifier if exists
     */
    public <T> T get(String id) {
        return (T) (id != null ? byId.get(id) : null);
    }

    /**
     * @return all the resources registered in this registry
     */
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(byId);
    }

    /**
     * Clear all registered resources
     *
     * @return this {@link ResourceRegistry}
     */
    public ResourceRegistry clear() {
        byId.clear();
        return this;
    }

}

