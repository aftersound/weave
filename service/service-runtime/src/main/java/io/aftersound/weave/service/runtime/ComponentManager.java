package io.aftersound.weave.service.runtime;

import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This {@link ComponentManager} manages the lifecycle of {@link ComponentConfig} (s) and
 * interacts with {@link ComponentRegistry} to manage the lifecycle of corresponding
 * components.
 */
final class ComponentManager extends WithConfigAutoRefreshMechanism implements Manageable<ComponentConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentManager.class);

    private static final boolean TOLERATE_EXCEPTION = true;

    private final String name;
    private final ConfigProvider<ComponentConfig> componentConfigProvider;
    private final ComponentRegistry componentRegistry;

    protected volatile Map<String, ComponentConfig> componentConfigById = Collections.emptyMap();

    public ComponentManager(
            String name,
            ConfigProvider<ComponentConfig> componentConfigProvider,
            ConfigUpdateStrategy configUpdateStrategy,
            ComponentRegistry componentRegistry) {
        super(configUpdateStrategy);

        this.name = name;
        this.componentConfigProvider = componentConfigProvider;
        this.componentRegistry = componentRegistry;
    }

    @Override
    synchronized void loadConfigs(boolean tolerateException) {
        // load client configs from provider
        List<ComponentConfig> componentConfigList = Collections.emptyList();
        try {
            componentConfigList = componentConfigProvider.getConfigList();
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading component configs from provider", e);
            if (tolerateException) {
                return;
            } else {
                throwException(e);
            }
        }

        // natural order has to be retained because one ComponentConfig might depend on component/ComponentConfig ahead of it.
        Map<String, ComponentConfig> componentConfigById = new LinkedHashMap<>();
        for (ComponentConfig componentConfig : componentConfigList) {
            componentConfigById.put(componentConfig.getId(), componentConfig);
        }

        // identify removed
        Map<String, ComponentConfig> removed = figureOutRemoved(this.componentConfigById, componentConfigById);

        this.componentConfigById = componentConfigById;

        // initialize component for each loaded ComponentConfig
        for (ComponentConfig componentConfig : componentConfigById.values()) {
            try {
                componentRegistry.initializeComponent(componentConfig);
            } catch (Exception e) {
                LOGGER.error(
                        "Exception occurred when initializing component of type {} with id {}",
                        componentConfig.getType(),
                        componentConfig.getId(),
                        e
                );
                if (!tolerateException) {
                    throwException(e);
                }
            }
        }

        // destroy components to be removed
        for (ComponentConfig componentConfig : removed.values()) {
            try {
                componentRegistry.destroyComponent(componentConfig.getType(), componentConfig.getId());
            } catch (Exception e) {
                LOGGER.error(
                        "Exception occurred when destroying component of type {} with id {}",
                        componentConfig.getType(),
                        componentConfig.getId(),
                        e
                );
            }
        }
    }

    @Override
    public ManagementFacade<ComponentConfig> getManagementFacade() {

        return new ManagementFacade<ComponentConfig>() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<ComponentConfig> entityType() {
                return ComponentConfig.class;
            }

            @Override
            public void refresh() {
                loadConfigs(TOLERATE_EXCEPTION);
            }

            @Override
            public List<ComponentConfig> list() {
                return new ArrayList<>(componentConfigById.values());
            }

            @Override
            public ComponentConfig get(String id) {
                return componentConfigById.get(id);
            }

        };

    }

    private void throwException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }

    private Map<String, ComponentConfig> figureOutRemoved(Map<String, ComponentConfig> existing, Map<String, ComponentConfig> latest) {
        Set<String> retained = new HashSet<>(existing.keySet());
        retained.retainAll(latest.keySet());

        Set<String> removed = new HashSet<>(existing.keySet());
        removed.removeAll(retained);

        Map<String, ComponentConfig> result = new HashMap<>(removed.size());
        for (String id : removed) {
            result.put(id, existing.get(id));
        }
        return result;
    }

}
