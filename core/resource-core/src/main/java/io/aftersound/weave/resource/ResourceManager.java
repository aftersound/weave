package io.aftersound.weave.resource;

/**
 * ResourceInitializer is responsible for managing lifecycle of one or more resources
 */
public interface ResourceManager {

    /**
     * @return
     *          resource declaration
     */
    ResourceDeclaration getDeclaration();

}
