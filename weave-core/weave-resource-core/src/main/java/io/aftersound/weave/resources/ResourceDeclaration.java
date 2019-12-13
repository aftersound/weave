package io.aftersound.weave.resources;

public interface ResourceDeclaration {

    /**
     * @return
     *      an array of ResourceType(s) this initializer depends on in order to do initialization work properly.
     *      Null is not allowed
     */
    ResourceType<?>[] getDependingResourceTypes();

    /**
     * @return
     *      an array of ResourceType(s) this initializer can create and initialize while other initializers might
     *      also create and initialize. Null is not allowed
     */
    ResourceType<?>[] getShareableResourceTypes();

    /**
     * @return
     *      an array of ResourceType(s) that this initializer will create and initialize. Null is not allowed.
     */
    ResourceType<?>[] getResourceTypes();

}
