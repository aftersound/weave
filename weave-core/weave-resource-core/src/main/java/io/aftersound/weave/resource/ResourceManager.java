package io.aftersound.weave.resource;

/**
 * ResourceInitializer is responsible for managing lifecycle of one or more resources
 * @param <RESOURCE_CONFIG>
 *          resource config in generic type
 */
public interface ResourceManager<RESOURCE_CONFIG extends ResourceConfig> {

    /**
     * @return
     *          resource declaration
     */
    ResourceDeclaration getDeclaration();

    /**
     * initialize resources in according to given resource config
     * @param managedResources
     *          holds any dependant resource and any resources that will be created and initialized by this
     * @param resourceConfig
     *          configuration needed to initialize resources
     */
    void initializeResources(ManagedResources managedResources, RESOURCE_CONFIG resourceConfig) throws Exception;

    /**
     * destroy resources in according to given resource config
     * @param managedResources
     *          holds any dependant resource and any resources that will be destroyed by this
     * @param resourceConfig
     *          configuration needed to destroy resources
     */
    void destroyResources(ManagedResources managedResources, RESOURCE_CONFIG resourceConfig) throws Exception;
}
