package io.aftersound.dependency;

import io.aftersound.common.NamedType;

import java.util.Collection;

public interface Declaration {

    /**
     * @return a collection of {@link NamedType} required by a component. Null is not allowed
     */
    Collection<NamedType<?>> getRequired();

    /**
     * Check if the component with given identifier is required
     *
     * @param id the identifier of target component
     * @return the component with given identifier, if required
     */
    boolean isRequired(String id);

    /**
     * @return identifiers of required components
     */
    Collection<String> requiredIds();
}
