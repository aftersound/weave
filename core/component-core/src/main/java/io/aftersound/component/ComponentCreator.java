package io.aftersound.component;

/**
 * Conceptual entity which creates component with specified component config
 *
 * @param <COMPONENT> type of component in generic form
 */
public interface ComponentCreator<COMPONENT> {

    /**
     * Creates component with specified config
     *
     * @param componentConfig component config in form of {@link ComponentConfig}
     * @return a component created
     */
    COMPONENT create(ComponentConfig componentConfig);
}
