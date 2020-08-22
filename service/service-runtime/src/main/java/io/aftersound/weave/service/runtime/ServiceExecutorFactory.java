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
    private final Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    private final ConfigProvider<ResourceDeclarationOverride> resourceDeclarationOverridesProvider;
    private final ConfigProvider<ResourceConfig> resourceConfigProvider;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();


    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<ResourceConfig> resourceConfigProvider) {
        this.name = name;
        this.managedResources = managedResources;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceDeclarationOverridesProvider = new ConfigProvider<ResourceDeclarationOverride>() {

            @Override
            protected List<ResourceDeclarationOverride> getConfigList() {
                return Collections.emptyList();
            }

        };
        this.resourceConfigProvider = resourceConfigProvider;
    }

    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<ResourceDeclarationOverride> resourceDeclarationOverridesProvider,
            ConfigProvider<ResourceConfig> resourceConfigProvider) {
        this.name = name;
        this.managedResources = managedResources;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceDeclarationOverridesProvider = resourceDeclarationOverridesProvider;
        this.resourceConfigProvider = resourceConfigProvider;
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        Map<String, ResourceDeclaration> resourceDelcarationOverrides = getResourceDeclarationOverrides();
        List<ResourceConfig> resourceConfigList = resourceConfigProvider.getConfigList();
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            LOGGER.info("");
            LOGGER.info("Instantiating {}", type.getName());

            ManagedResources specificResources = createAndInitServiceExecutorSpecificResources(
                    type,
                    resourceDelcarationOverrides.get(type.getName()),
                    resourceConfigList
            );
            ServiceExecutor executor = type.getDeclaredConstructor(ManagedResources.class).newInstance(specificResources);
            serviceExecutorByType.put(executor.getType(), executor);

            LOGGER.info(
                    "as {} and bound with type name '{}'",
                    executor,
                    executor.getType()
            );
        }
    }

    public ServiceExecutor getServiceExecutor(ServiceMetadata serviceMetadata) {
        return serviceExecutorByType.get(serviceMetadata.getExecutionControl().getType());
    }

    private Map<String, ResourceDeclaration> getResourceDeclarationOverrides() throws Exception {
        Map<String, ResourceDeclaration> rdoByServiceExecutorType = new HashMap<>();
        List<ResourceDeclarationOverride> rdoList = resourceDeclarationOverridesProvider.getConfigList();
        if (rdoList != null) {
            for (ResourceDeclarationOverride rdo : rdoList) {
                rdoByServiceExecutorType.put(rdo.getServiceExecutor(), rdo.resourceDeclaration());
            }
        }
        return rdoByServiceExecutorType;
    }

    private ManagedResources createAndInitServiceExecutorSpecificResources(
            Class<? extends ServiceExecutor> type,
            ResourceDeclaration resourceDeclarationOverride,
            List<ResourceConfig> resourceConfigs) throws Exception {

        ManagedResources serviceOnlyResources = new ManagedResourcesImpl();

        ResourceManager resourceManager = getResourceManager(type);
        LOGGER.info("...resource manager is {}", resourceManager);

        ResourceDeclaration resourceDeclaration;
        if (resourceDeclarationOverride != null) {
            LOGGER.info("...ResourceDeclaration is overridden");
            resourceDeclaration = resourceDeclarationOverride;
        } else {
            resourceDeclaration = resourceManager.getDeclaration();
        }

        // 1.populate resources that current resourceManager depends on
        for (ResourceType<?> resourceType : resourceDeclaration.getRequiredResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            if (resource == null) {
                throw new Exception("Missing " + resourceType + " needed by " + resourceManager.getClass().getName() + " for " + type.getName());
            } else {
                LOGGER.info(
                        "...resource ({},{}) is available as required",
                        resourceType.name(),
                        resourceType.type().getName(),
                        type.getName()
                );
                serviceOnlyResources.setResource(resourceType.name(), resource);
            }

        }

        // 2.populate shareable resources that resourceInitializer can initialize, to avoid duplicated initialization
        for (ResourceType<?> resourceType : resourceDeclaration.getShareableResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            if (resource != null) {
                LOGGER.info(
                        "...resource ({},{}) is available as shareable",
                        resourceType.name(),
                        resourceType.type().getName(),
                        type.getName()
                );
                serviceOnlyResources.setResource(resourceType.name(), resource);
            }
        }

        // 3.initialize
        for (ResourceConfig resourceConfig : resourceConfigs) {
            if (resourceManager.accept(resourceConfig)) {
                LOGGER.info(
                        "...additional required resources are being initialized by {}",
                        resourceManager
                );
                resourceManager.initializeResources(serviceOnlyResources, resourceConfig);
            }
        }

        // 4.place shareable resources in common managedResources
        for (ResourceType<?> resourceType : resourceDeclaration.getShareableResourceTypes()) {
            if (managedResources.getResource(resourceType) == null) {
                LOGGER.info(
                        "...resource ({},{}) is exported as shared resource",
                        resourceType.name(),
                        resourceType.type().getName(),
                        type.getName()
                );
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
            LOGGER.warn("...RESOURCE_MANAGER is not declared");
            return NULL_RESOURCE_MANAGER;
        }

        try {
            Object obj = field.get(null);
            if (obj instanceof ResourceManager) {
                return (ResourceManager)obj;
            } else {
                LOGGER.warn(
                        "...RESOURCE_MANAGER is not of type {}",
                        type.getName(),
                        ResourceManager.class.getName()
                );
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "...RESOURCE_MANAGER cannot be accessed",
                    type.getName(),
                    e
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
