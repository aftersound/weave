package io.aftersound.weave.resource;

import java.util.Collection;

/**
 * Conceptual container which holds resources required by certain actor instance
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
     * Set resource for request-time usage.
     * @param type
     *          - type of resource
     * @param resource
     *          - resource
     * @param <R>
     *          - generic form of resource type
     */
    <R> void setResource(ResourceType<R> type, R resource);

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
