package io.aftersound.weave.resource;

public interface ResourceDeclaration {

    /**
     * @return
     *      an array of ResourceType(s) this initializer depends on in order to do initialization work properly.
     *      Null is not allowed
     */
    ResourceType<?>[] getRequiredResourceTypes();

    /**
     * @return
     *      an array of ResourceType(s) this initializer can create, initialize and export to share.
     *      Null is not allowed
     */
    ResourceType<?>[] getShareableResourceTypes();

    /**
     * @return
     *      an array of ResourceType(s) that this initializer will create and initialize. Null is not allowed.
     */
    ResourceType<?>[] getResourceTypes();

}
