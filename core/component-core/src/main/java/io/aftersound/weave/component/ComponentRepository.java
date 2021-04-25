package io.aftersound.weave.component;

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
}
