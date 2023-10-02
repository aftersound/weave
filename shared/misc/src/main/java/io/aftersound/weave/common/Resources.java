package io.aftersound.weave.common;

/**
 * Conceptual container which holds resources and provides access
 */
public interface Resources {

    /**
     * Get resource of expected type with given identifier
     *
     * @param id  resource identifier
     * @param <T> expected type in generic form
     * @return the resource with given identifier if exists
     */
    <T> T get(String id);

}
