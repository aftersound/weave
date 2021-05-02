package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.dependency.Declaration;
import io.aftersound.weave.dependency.SimpleDeclaration;
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

    private static final Declaration EMPTY_DEPENDENCY_DECLARATION = SimpleDeclaration.withRequired();

    private final ComponentRepository componentRepository;
    private final Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    private final ConfigProvider<DependencyDeclarationOverride> dependencyDeclarationOverridesProvider;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();


    ServiceExecutorFactory(
            ComponentRepository componentRepository,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes) {
        this.componentRepository = componentRepository;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.dependencyDeclarationOverridesProvider = new ConfigProvider<DependencyDeclarationOverride>() {

            @Override
            protected List<DependencyDeclarationOverride> getConfigList() {
                return Collections.emptyList();
            }

        };
    }

    ServiceExecutorFactory(
            ComponentRepository componentRepository,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes,
            ConfigProvider<DependencyDeclarationOverride> dependencyDeclarationOverridesProvider) {
        this.componentRepository = componentRepository;
        this.serviceExecutorTypes = serviceExecutorTypes;
        this.dependencyDeclarationOverridesProvider = dependencyDeclarationOverridesProvider;
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        LOGGER.info("List of managed components");
        for (String componentId : componentRepository.componentIds()) {
            LOGGER.info("...{}", componentId);
        }

        Map<String, Declaration> dependencyDeclarationOverrides = getDependencyDeclarationOverrides();
        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            LOGGER.info("");
            LOGGER.info("Instantiating {}", type.getName());

            ComponentRepository specificComponentRepository = createAndInitServiceExecutorSpecificComponentRepository(
                    type,
                    dependencyDeclarationOverrides.get(type.getName())
            );
            ServiceExecutor executor = type.getDeclaredConstructor(ComponentRepository.class).newInstance(specificComponentRepository);
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

    private Map<String, Declaration> getDependencyDeclarationOverrides() throws Exception {
        Map<String, Declaration> ddoByServiceExecutorType = new HashMap<>();
        List<DependencyDeclarationOverride> ddoList = dependencyDeclarationOverridesProvider.getConfigList();
        if (ddoList != null) {
            for (DependencyDeclarationOverride ddo : ddoList) {
                ddoByServiceExecutorType.put(ddo.getServiceExecutor(), ddo.dependencyDeclaration());
            }
        }
        return ddoByServiceExecutorType;
    }

    private ComponentRepository createAndInitServiceExecutorSpecificComponentRepository(
            Class<? extends ServiceExecutor> type,
            Declaration dependencyDeclarationOverride) throws Exception {

        Declaration declaration;
        if (dependencyDeclarationOverride != null) {
            LOGGER.info("...Dependency declaration is overridden");
            declaration = dependencyDeclarationOverride;
        } else {
            declaration = getDependencyDeclaration(type);
        }

        // verify if the components required by this service executor exist.
        for (NamedType<?> dependencyNamedType : declaration.getRequired()) {
            Object component = componentRepository.getComponent(dependencyNamedType);
            if (component == null) {
                throw new Exception("Missing " + dependencyNamedType + " required by " + type.getName());
            } else {
                LOGGER.info(
                        "...component ({},{}) is available as required",
                        dependencyNamedType.name(),
                        dependencyNamedType.type().getName(),
                        type.getName()
                );
            }
        }

        return new ManagedComponentRepository(declaration, componentRepository);
    }

    /**
     * Get a {@link Declaration} for given type of {@link ServiceExecutor}, which is optional.
     * @param type
     *          type of ServiceExecutor
     * @return
     *          a {@link Declaration} paired with given type of {@link ServiceExecutor}
     */
    private Declaration getDependencyDeclaration(Class<? extends ServiceExecutor> type) {
        Field field;
        try {
            // implicit contract here
            // ServiceExecutor.DEPENDENCY_DECLARATION is optional
            field = type.getDeclaredField("DEPENDENCY_DECLARATION");
        } catch (NoSuchFieldException e) {
            LOGGER.warn("...DEPENDENCY_DECLARATION is not declared");
            return EMPTY_DEPENDENCY_DECLARATION;
        }

        try {
            Object obj = field.get(null);
            if (obj instanceof Declaration) {
                return (Declaration) obj;
            } else {
                LOGGER.warn(
                        "...DEPENDENCY_DECLARATION is of type {}, not of expected type {}",
                        type.getName(),
                        Declaration.class.getName()
                );
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "...DEPENDENCY_DECLARATION cannot be accessed in {}",
                    type.getName(),
                    e
            );
        }
        return EMPTY_DEPENDENCY_DECLARATION;
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
