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

    private static final ResourceDeclaration EMPTY_RESOURCE_DECLARATION = SimpleResourceDeclaration.withRequired();

    private final String name;
    private final ManagedResources managedResources;
    private final Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    private final ConfigProvider<ResourceDeclarationOverride> resourceDeclarationOverridesProvider;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();


    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes) {
        this.name = name;
        this.managedResources = managedResources;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceDeclarationOverridesProvider = new ConfigProvider<ResourceDeclarationOverride>() {

            @Override
            protected List<ResourceDeclarationOverride> getConfigList() {
                return Collections.emptyList();
            }

        };
    }

    ServiceExecutorFactory(
            String name,
            ManagedResources managedResources,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<ResourceDeclarationOverride> resourceDeclarationOverridesProvider) {
        this.name = name;
        this.managedResources = managedResources;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.resourceDeclarationOverridesProvider = resourceDeclarationOverridesProvider;
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        Map<String, ResourceDeclaration> resourceDelcarationOverrides = getResourceDeclarationOverrides();
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            LOGGER.info("");
            LOGGER.info("Instantiating {}", type.getName());

            ManagedResources specificResources = createAndInitServiceExecutorSpecificResources(
                    type,
                    resourceDelcarationOverrides.get(type.getName())
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
            ResourceDeclaration resourceDeclarationOverride) throws Exception {

        ManagedResources serviceOnlyResources = new ManagedResourcesImpl();

        ResourceDeclaration resourceDeclaration;
        if (resourceDeclarationOverride != null) {
            LOGGER.info("...ResourceDeclaration is overridden");
            resourceDeclaration = resourceDeclarationOverride;
        } else {
            resourceDeclaration = getResourceDeclaration(type);
        }

        // populate resources that current resourceManager depends on/requires
        for (ResourceType<?> resourceType : resourceDeclaration.getRequiredResourceTypes()) {
            Object resource = managedResources.getResource(resourceType);
            if (resource == null) {
                throw new Exception("Missing " + resourceType + " required by " + type.getName());
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

        return serviceOnlyResources;
    }

    /**
     * Get a {@link ResourceDeclaration} for given type of {@link ServiceExecutor}, which is optional.
     * @param type
     *          type of ServiceExecutor
     * @return
     *          a {@link ResourceDeclaration} paired with given type of {@link ServiceExecutor}
     */
    private ResourceDeclaration getResourceDeclaration(Class<? extends ServiceExecutor> type) {
        Field field;
        try {
            // implicit contract here
            // ServiceExecutor.RESOURCE_DECLARATION is optional
            field = type.getDeclaredField("RESOURCE_DECLARATION");
        } catch (NoSuchFieldException e) {
            LOGGER.warn("...RESOURCE_DECLARATION is not declared");
            return EMPTY_RESOURCE_DECLARATION;
        }

        try {
            Object obj = field.get(null);
            if (obj instanceof ResourceDeclaration) {
                return (ResourceDeclaration)obj;
            } else {
                LOGGER.warn(
                        "...RESOURCE_DECLARATION is of type {}, not of expected type {}",
                        type.getName(),
                        ResourceDeclaration.class.getName()
                );
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "...RESOURCE_DECLARATION cannot be accessed in {}",
                    type.getName(),
                    e
            );
        }
        return EMPTY_RESOURCE_DECLARATION;
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
