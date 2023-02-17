package io.aftersound.weave.component;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maintains {@link ComponentFactory} for known components.
 */
public class ComponentFactoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentFactoryRegistry.class);

    private final Object lock = new Object();

    private final ComponentRegistry componentRegistry;
    private final ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings;

    private Map<String, ComponentFactory<?>> factoryByComponentType = new HashMap<>();

    ComponentFactoryRegistry(
            ComponentRegistry componentRegistry,
            ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings) {
        this.componentRegistry = componentRegistry;
        this.componentFactoryBindings = componentFactoryBindings;
    }

    ComponentFactoryRegistry initialize() throws Exception {
        synchronized (lock) {
            NamedTypes<ComponentConfig> controlTypes = componentFactoryBindings.controlTypes();
            for (String name : controlTypes.names()) {
                Class<?> factoryType = componentFactoryBindings.getActorType(name);
                ComponentFactory<?> factory = createFactory0(factoryType);
                factoryByComponentType.put(name, factory);
            }
        }
        return this;
    }

    private ComponentFactory<?> createFactory0(Class<?> factoryType) throws Exception {
        try {
            Constructor<? extends ComponentFactory<?>> constructor =
                    (Constructor<? extends ComponentFactory<?>>)factoryType.getDeclaredConstructor(ComponentRegistry.class);
            return constructor.newInstance(componentRegistry);
        } catch (Exception any) {
            LOGGER.error("failed to instantiate an instance of " + factoryType, any);
            throw any;
        }
    }

    public <COMPONENT, FACTORY extends ComponentFactory<COMPONENT>> FACTORY getComponentFactory(String name) throws Exception {
        return (FACTORY) factoryByComponentType.get(name);
    }

    public Class<Object> getComponentType(String name) {
        return componentFactoryBindings.getProductTypeByName(name);
    }

}
