package io.aftersound.weave.service.resources;

import io.aftersound.weave.service.ServiceExecutor;

import java.util.Collection;

/**
 * Conceptual container which holds resources required by instance of {@link ServiceExecutor}
 */
public interface ManagedResources {

    /**
     * Set resource for request-time usage.
     * @param name
     *          - name of resource
     * @param resource
     *          - resource
     */
    void setResource(String name, Object resource);

    /**
     * Get resource with given {@link ResourceType}
     * @param resourceType
     *          - resource type
     * @param <R>
     *          - generic type of resource
     * @return
     *          resource of specified type if exists.
     */
    <R> R getResource(ResourceType<R> resourceType);

    /**
     * Get resource with given name and type
     * @param name
     *          - name of resource
     * @param resourceType
     *          - type/class of resource
     * @param <R>
     *          - generic type of resource
     * @return
     *          resource with specified name and type if exists.
     */
    <R> R getResource(String name, Class<R> resourceType);

    /**
     * @return
     *          all names of resources in this container.
     */
    Collection<String> resourceNames();
}
