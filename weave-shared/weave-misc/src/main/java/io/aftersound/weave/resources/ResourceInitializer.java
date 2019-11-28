package io.aftersound.weave.resources;

import io.aftersound.weave.config.Config;

/**
 * ResourceInitializer is responsible for creating and initializing resources
 */
public interface ResourceInitializer {

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

    /**
     * initialize resources
     * @param managedResources
     *          holds any dependant resource and any resources that will be created and initialized by this
     * @param resourceConfig
     *          configuration needed to initialize resources
     */
    void initializeResources(ManagedResources managedResources, Config resourceConfig) throws Exception;
}
