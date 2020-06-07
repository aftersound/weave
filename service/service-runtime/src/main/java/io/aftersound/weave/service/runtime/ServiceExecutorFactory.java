package io.aftersound.weave.service.runtime;

import io.aftersound.weave.resource.*;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This {@link ServiceExecutorFactory} manages the lifecycle of {@link ServiceExecutor} (s)
 * and also provides a registry which returns {@link ServiceExecutor} for given
 * {@link ServiceMetadata}.
 */
public class ServiceExecutorFactory implements Initializer, Manageable<ServiceExecutor.Info> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutorFactory.class);

    private static final ResourceManager NULL_RESOURCE_MANAGER = new NullResourceManager();

    private final String name;
    private final ManagedResources managedResources;
    private final Map<String, ResourceDeclaration> resourceDeclarationOverrides;
    private final Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    private final ConfigProvider<ResourceConfig> resourceConfigProvider;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();

    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<ResourceConfig> resourceConfigProvider) {
        this.name = name;
        this.managedResources = managedResources;
        this.resourceDeclarationOverrides = Collections.emptyMap();
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceConfigProvider = resourceConfigProvider;
    }

    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Map<String, ResourceDeclaration> resourceDeclarationOverrides,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<ResourceConfig> resourceConfigProvider) {
        this.name = name;
        this.managedResources = managedResources;
        this.resourceDeclarationOverrides = resourceDeclarationOverrides != null ?
                Collections.unmodifiableMap(resourceDeclarationOverrides) : Collections.emptyMap();
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceConfigProvider = resourceConfigProvider;
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        List<ResourceConfig> resourceConfigList = resourceConfigProvider.getConfigList();
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            ManagedResources specificResources = createAndInitServiceExecutorSpecificResources(type, resourceConfigList);
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
        ResourceDeclaration resourceDeclaration = getAndOverrideResourceDeclarationIfEligible(type, resourceManager);

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
            if (resourceManager.accept(resourceConfig)) {
                resourceManager.initializeResources(serviceOnlyResources, resourceConfig);
            } else {
                resourceManager.initializeResources(serviceOnlyResources, null);
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

    private ResourceDeclaration getAndOverrideResourceDeclarationIfEligible(
            Class<? extends ServiceExecutor> serviceExecutorType,
            ResourceManager resourceManager) {
        ResourceDeclaration resourceDeclaration = resourceManager.getDeclaration();

        if (!resourceDeclarationOverrides.containsKey(serviceExecutorType.getName())) {
            return resourceDeclaration;
        }

        return resourceDeclarationOverrides.get(serviceExecutorType.getName());
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

    @Override
    public ManagementFacade<ServiceExecutor.Info> getManagementFacade() {

        return new ManagementFacade<ServiceExecutor.Info>() {

            @Override
            public String name() {
                return null;
            }

            @Override
            public Class<ServiceExecutor.Info> entityType() {
                return ServiceExecutor.Info.class;
            }

            @Override
            public void refresh() {
                // Do nothing
            }

            @Override
            public List<ServiceExecutor.Info> list() {
                List<ServiceExecutor.Info> infoList = new ArrayList<>();
                for (Map.Entry<String, ServiceExecutor> e : serviceExecutorByType.entrySet()) {
                    infoList.add(e.getValue().getInfo());
                }
                return infoList;
            }

            @Override
            public ServiceExecutor.Info get(String id) {
                ServiceExecutor serviceExecutor = serviceExecutorByType.get(id);
                if (serviceExecutor != null) {
                    return serviceExecutor.getInfo();
                } else {
                    return null;
                }
            }
        };
    }
}
