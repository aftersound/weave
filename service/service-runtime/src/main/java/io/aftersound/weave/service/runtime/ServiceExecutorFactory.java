package io.aftersound.weave.service.runtime;

import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This {@link ServiceExecutorFactory} manages the lifecycle of {@link ServiceExecutor} (s)
 * and also provides a registry which returns {@link ServiceExecutor} for given
 * {@link ServiceMetadata}.
 */
public class ServiceExecutorFactory implements Initializer, Manageable<ServiceExecutor.Info> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExecutorFactory.class);

    private final ComponentRepository componentRepository;
    private final Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    private final Map<String, ServiceExecutor> serviceExecutorByType = new HashMap<>();

    ServiceExecutorFactory(
            ComponentRepository componentRepository,
            Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes) {
        this.componentRepository = componentRepository;
        this.serviceExecutorTypes = serviceExecutorTypes;
    }

    @Override
    public void init(boolean tolerateException) throws Exception {
        LOGGER.info("List of managed components");
        for (String componentId : componentRepository.componentIds()) {
            LOGGER.info("...{}", componentId);
        }

        for (Class<? extends ServiceExecutor> type : serviceExecutorTypes) {
            LOGGER.info("");
            LOGGER.info("Instantiating {}", type.getName());

            ServiceExecutor executor = type.getDeclaredConstructor(ComponentRepository.class).newInstance(componentRepository);
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
