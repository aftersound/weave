package io.aftersound.weave.resource;

public interface ResourceDeclaration {

    /**
     * @return
     *      an array of ResourceType(s) required by a component. Null is not allowed
     */
    ResourceType<?>[] getRequiredResourceTypes();

}
