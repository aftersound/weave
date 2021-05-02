package io.aftersound.weave.component;

import io.aftersound.weave.common.NamedType;

import java.util.Collection;

/**
 * A conceptual repository which
 *   1.holds components
 *   2.provides access to components by identifiers
 */
public interface ComponentRepository {
    
    /**
     * Get the component with specified identifier, if exists
     *
     * @param id          the identifier of target component
     * @param <COMPONENT> desired type of component in generic form
     * @return the component with specified identifier
     */
    <COMPONENT> COMPONENT getComponent(String id);

    /**
     * Get the component with specified identifier and type, if exists
     *
     * @param id          the identifier of target component
     * @param type        the expected type of component
     * @param <COMPONENT> the expected type of component in generic form
     * @return the component with specified identifier and type
     */
    <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type);

    /**
     * Get component with given {@link NamedType}
     *
     * @param namedType - {@link NamedType} of component
     * @param <C>       - generic type of component
     * @return component of specified type if exists.
     */
    <C> C getComponent(NamedType<C> namedType);

    /**
     * @return identifiers of all components in this repository
     */
    Collection<String> componentIds();
}
