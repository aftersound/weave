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

    /**
     * Check if given {@link ResourceConfig} is acceptable
     * @param resourceConfig
     *          - {@link ResourceConfig} to be checked
     * @return
     *          true or false
     */
    boolean accept(ResourceConfig resourceConfig);

    /**
     * initialize resources in according to given resource config
     * @param managedResources
     *          holds any dependant resource and any resources that will be created and initialized by this
     * @param resourceConfig
     *          configuration needed to initialize resources
     */
    void initializeResources(ManagedResources managedResources, ResourceConfig resourceConfig) throws Exception;

    /**
     * destroy resources in according to given resource config
     * @param managedResources
     *          holds any dependant resource and any resources that will be destroyed by this
     * @param resourceConfig
     *          configuration needed to destroy resources
     */
    void destroyResources(ManagedResources managedResources, ResourceConfig resourceConfig) throws Exception;
}
