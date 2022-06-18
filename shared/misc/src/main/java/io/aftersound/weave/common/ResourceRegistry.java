package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resource registry which holds resources to be referenced by identifier
 */
public final class ResourceRegistry implements Serializable {

    private static Map<String, Object> resourceById = new ConcurrentHashMap<>();

    /**
     * Register the resource with specified identifier
     *
     * @param id       resource identifier
     * @param resource resource object
     * @param <T>      type of resource in generic form
     */
    public static <T> void register(String id, T resource) {
        resourceById.put(id, resource);
    }

    /**
     * Get resource of expected type with given identifier
     *
     * @param id  resource identifier
     * @param <T> expected type in generic form
     * @return the resource with given identifier if exists
     */
    public static <T> T get(String id) {
        return (T) (id != null ? resourceById.get(id) : null);
    }

}

