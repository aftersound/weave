package io.aftersound.weave.component;

import io.aftersound.weave.common.NamedType;

import java.util.Collection;

/**
 * Conceptual container which holds components
 */
public interface ManagedComponents {

    /**
     * Set component for request-time usage.
     * @param name
     *          - name of component
     * @param component
     *          - instance of component
     */
    void setComponent(String name, Object component);

    /**
     * Set component for request-time usage.
     * @param type
     *          - {@link NamedType} of component
     * @param component
     *          - instance of component
     * @param <C>
     *          - generic form of component type
     */
    <C> void setComponent(NamedType<C> type, C component);

    /**
     * Get component with given {@link NamedType}
     * @param namedType
     *          - {@link NamedType} of component
     * @param <C>
     *          - generic type of component
     * @return
     *          component of specified type if exists.
     */
    <C> C getComponent(NamedType<C> namedType);

    /**
     * Get component with given name and type
     * @param name
     *          - name of component
     * @param componentType
     *          - type/class of component
     * @param <C>
     *          - generic type of resource
     * @return
     *          resource with specified name and type if exists.
     */
    <C> C getComponent(String name, Class<C> componentType);

    /**
     * @return
     *          all names of components in this container.
     */
    Collection<String> componentNames();
}
