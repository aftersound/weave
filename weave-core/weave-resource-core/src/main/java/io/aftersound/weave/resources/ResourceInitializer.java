package io.aftersound.weave.resources;

import io.aftersound.weave.config.Config;

/**
 * ResourceInitializer is responsible for creating and initializing resources
 */
public interface ResourceInitializer extends ResourceDeclaration {

    /**
     * initialize resources
     * @param managedResources
     *          holds any dependant resource and any resources that will be created and initialized by this
     * @param resourceConfig
     *          configuration needed to initialize resources
     */
    void initializeResources(ManagedResources managedResources, Config resourceConfig) throws Exception;
}
