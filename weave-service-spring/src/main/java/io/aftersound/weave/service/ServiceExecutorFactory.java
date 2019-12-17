package io.aftersound.weave.service;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.resource.*;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link ServiceExecutorFactory} manages the lifecycle of {@link ServiceExecutor} (s)
 * and also provides a registry which returns {@link ServiceExecutor} for given
 * {@link ServiceMetadata}.
 */
public class ServiceExecutorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutorFactory.class);

    private static final ResourceManager NULL_RESOURCE_MANAGER = new NullResourceManager();

    private final ManagedResources managedResources;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();

    public ServiceExecutorFactory(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    public void init(
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            List<ResourceConfig> resourceConfigs) throws Exception {
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            ManagedResources specificResources = createAndInitServiceExecutorSpecificResources(type, resourceConfigs);
            ServiceExecutor executor = type.getDeclaredConstructor(ManagedResources.class).newInstance(specificResources);
            serviceExecutorByType.put(executor.getType(), executor);
        }
    }

    public ServiceExecutor getServiceExecutor(ServiceMetadata serviceMetadata) {
        return serviceExecutorByType.get(serviceMetadata.getExecutionControl().getType());
    }

    private ManagedResources createAndInitServiceExecutorSpecificResources(
            Class<? extends ServiceExecutor> type,
            List<ResourceConfig> resourceConfigs) throws Exception {

        ManagedResources serviceOnlyResources = new ManagedResourcesImpl();

        ResourceManager resourceManager = getResourceManager(type);
        ResourceDeclaration resourceDeclaration = resourceManager.getDeclaration();

        // 1.populate resources that current resourceManager depends on
        for (ResourceType<?> resourceType : resourceDeclaration.getDependingResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            if (resource == null) {
                throw new Exception("Missing " + resourceType + " needed by " + resourceManager.getClass().getName() + " for " + type.getName());
            }
            serviceOnlyResources.setResource(resourceType.name(), resource);
        }

        // 2.populate shareable resources that resourceInitializer can initialize, to avoid duplicated initialization
        for (ResourceType<?> resourceType : resourceDeclaration.getShareableResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            serviceOnlyResources.setResource(resourceType.name(), resource);
        }

        // 3.initialize
        ResourceType<?>[] resourceTypes = resourceDeclaration.getResourceTypes();
        for (ResourceConfig resourceConfig : resourceConfigs) {
            if (forResourceManager(resourceConfig, resourceTypes)) {
                resourceManager.initializeResources(serviceOnlyResources, resourceConfig);
            }
        }

        // 4.place shareable resources in common managedResources
        for (ResourceType<?> resourceType : resourceDeclaration.getShareableResourceTypes()) {
            if (managedResources.getResource(resourceType) == null) {
                managedResources.setResource(resourceType.name(), serviceOnlyResources.getResource(resourceType));
            }
        }

        return serviceOnlyResources;
    }

    /**
     * Get a {@link ResourceManager} for given type of {@link ServiceExecutor}, which is optional.
     * @param type
     *          type of ServiceExecutor
     * @return
     *          a {@link ResourceManager} paired with given type of {@link ServiceExecutor}
     */
    private ResourceManager getResourceManager(Class<? extends ServiceExecutor> type) {
        Field field;
        try {
            // implicit contract here
            // ServiceExecutor.RESOURCE_MANAGER is optional
            field = type.getDeclaredField("RESOURCE_MANAGER");
        } catch (NoSuchFieldException e) {
            LOGGER.warn("{}.RESOURCE_MANAGER is not declared", type.getName());
            return NULL_RESOURCE_MANAGER;
        }

        try {
            Object obj = field.get(null);
            if (obj instanceof ResourceManager) {
                return (ResourceManager)obj;
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "{}: Exception occurred when attempting to read {}.RESOURCE_INITIALIZER as ResourceInitializer",
                    e,
                    type.getName()
            );
        }
        return NULL_RESOURCE_MANAGER;
    }

    /**
     * Check if given {@link ResourceConfig} is for {@link ResourceManager} which
     * manages specified list of {@link ResourceType} (s).
     * @param resourceConfig
     *          a {@link ResourceConfig} which needs to be checked
     * @param resourceTypes
     *          {@link ResourceType} (s) associated with {@link ResourceManager}
     * @return
     *          check result
     */
    private boolean forResourceManager(
            ResourceConfig resourceConfig,
            ResourceType<?>[] resourceTypes) {
        String resourceName = resourceConfig.getName();
        if (resourceName == null && resourceName.isEmpty()) {
            return false;
        }

        for (ResourceType resourceType : resourceTypes) {
            if (resourceName.equals(resourceType.name())) {
                return true;
            }
        }
        return false;
    }

}
