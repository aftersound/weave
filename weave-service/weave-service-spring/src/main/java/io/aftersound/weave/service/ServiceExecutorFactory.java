package io.aftersound.weave.service;

import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.resources.NullResourceInitializer;
import io.aftersound.weave.resources.ResourceInitializer;
import io.aftersound.weave.resources.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This {@link ServiceExecutorFactory} manages the lifecycle of {@link ServiceExecutor} (s)
 * and also provides a registry which returns {@link ServiceExecutor} for given
 * {@link ServiceMetadata}.
 */
public class ServiceExecutorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutorFactory.class);

    private static final ResourceInitializer NULL_RESOURCE_INITIALIZER = new NullResourceInitializer();

    private final ManagedResources managedResources;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();

    public ServiceExecutorFactory(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    public void init(Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes) throws Exception {
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            ManagedResources specificResources = createAndInitServiceExecutorSpecificResources(type);
            ServiceExecutor executor = type.getDeclaredConstructor(ManagedResources.class).newInstance(specificResources);
            serviceExecutorByType.put(executor.getType(), executor);
        }
    }

    public ServiceExecutor getServiceExecutor(ServiceMetadata serviceMetadata) {
        return serviceExecutorByType.get(serviceMetadata.getExecutionControl().getType());
    }

    private ManagedResources createAndInitServiceExecutorSpecificResources(Class<? extends ServiceExecutor> type) throws Exception {

        ManagedResources serviceOnlyResources = new ManagedResourcesImpl();

        ResourceInitializer resourceInitializer = getResourceInitializer(type);

        // 1.populate resources that current resourceInitializer depends on
        for (ResourceType<?> resourceType : resourceInitializer.getDependingResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            if (resource == null) {
                throw new Exception("Missing " + resourceType + " needed by " + resourceInitializer.getClass().getName() + " for " + type.getName());
            }
            serviceOnlyResources.setResource(resourceType.name(), resource);
        }

        // 2.populate shareable resources that resourceInitializer can initialize, to avoid duplicated initialization
        for (ResourceType<?> resourceType : resourceInitializer.getShareableResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            serviceOnlyResources.setResource(resourceType.name(), resource);
        }

        // TODO: how to handle and pass config specific to {ServiceExecutor, ResourceInitializer} in context
        // 3.initialize
        resourceInitializer.initializeResources(serviceOnlyResources, null);

        // 4.place shareable resources in common managedResources
        for (ResourceType<?> resourceType : resourceInitializer.getShareableResourceTypes()) {
            if (managedResources.getResource(resourceType) == null) {
                managedResources.setResource(resourceType.name(), serviceOnlyResources.getResource(resourceType));
            }
        }

        return serviceOnlyResources;
    }

    /**
     * Get a {@link ResourceInitializer} for given type of {@link ServiceExecutor}, which is optional.
     * @param type
     *          type of ServiceExecutor
     * @return
     *          a {@link ResourceInitializer} paired with given type of {@link ServiceExecutor}
     */
    private ResourceInitializer getResourceInitializer(Class<? extends ServiceExecutor> type) {
        Field field;
        try {
            // implicit contract here
            // ServiceExecutor.RESOURCE_INITIALIZER is optional
            field = type.getDeclaredField("RESOURCE_INITIALIZER");
        } catch (NoSuchFieldException e) {
            LOGGER.warn("{}.RESOURCE_INITIALIZER is not declared", type.getName());
            return NULL_RESOURCE_INITIALIZER;
        }

        try {
            Object obj = field.get(null);
            if (obj instanceof ResourceInitializer) {
                return (ResourceInitializer)obj;
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "{}: Exception occurred when attempting to read {}.RESOURCE_INITIALIZER as ResourceInitializer",
                    e,
                    type.getName()
            );
        }
        return NULL_RESOURCE_INITIALIZER;
    }

}
