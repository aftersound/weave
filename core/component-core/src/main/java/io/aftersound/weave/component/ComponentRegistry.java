package io.aftersound.weave.component;

import io.aftersound.util.Key;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ComponentRegistry {

    private final ComponentFactoryRegistry cfr;
    private final Map<String, ComponentHandle<?>> componentHandleById = new HashMap<>();
    private final Map<String, SignatureGroup> signatureGroupByComponentFactoryType = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentRegistry.class);

    public ComponentRegistry(ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings) {
        try {
            this.cfr = new ComponentFactoryRegistry(this, componentFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ComponentRegistry(String... componentFactories) {
        try {
            ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings =
                    ActorBindingsUtil.loadActorBindings(
                            Arrays.asList(componentFactories),
                            ComponentConfig.class,
                            Object.class,
                            false
                    );
            this.cfr = new ComponentFactoryRegistry(this, componentFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <COMPONENT> ComponentHandle<COMPONENT> registerComponent(
            COMPONENT component,
            ComponentConfig config,
            Signature configSignature) {

        if (component == null) {
            return null;
        }

        if (config == null || config.getType() == null || config.getId() == null) {
            return null;
        }

        if (configSignature == null) {
            return null;
        }

        Collection<Key<?>> configKeys = ComponentConfigKeysRegistry.INSTANCE.getConfigKeys(config.getType());

        return (ComponentHandle<COMPONENT>) componentHandleById.put(
                config.getId(),
                ComponentHandle.of(component, config, configSignature, configKeys));
    }

    <COMPONENT> ComponentHandle<COMPONENT> getComponentHandle(String id) {
        return (ComponentHandle<COMPONENT>)componentHandleById.get(id);
    }

    <COMPONENT> ComponentHandle<COMPONENT> unregisterComponent(String id, Class<COMPONENT> componentType) {
        ComponentHandle<?> handle = componentHandleById.get(id);
        if (handle != null && componentType.isInstance(handle.component())) {
            return (ComponentHandle<COMPONENT>) componentHandleById.remove(id);
        } else {
            return null;
        }
    }

    void addSignatureGroup(String componentFactoryType, SignatureGroup signatureGroup) {
        signatureGroupByComponentFactoryType.put(componentFactoryType, signatureGroup);
    }

    SignatureGroup getSignatureGroup(String componentFactoryType) {
        return signatureGroupByComponentFactoryType.get(componentFactoryType);
    }

    public void initializeComponent(ComponentConfig config) throws Exception {
        LOGGER.info("");
        LOGGER.info("Creating component of type '{}' and identified as '{}'", config.getType(), config.getId());
        ComponentFactory<?> componentFactory = cfr.getComponentFactory(config.getType());
        if (componentFactory != null) {
            LOGGER.info("...using ComponentFactory {}", componentFactory);
        } else {
            LOGGER.error("...no ComponentFactory associated with type '{}' in registry", config.getType());
        }
        Object component = componentFactory.create(config);
        LOGGER.info("as {}", component);
    }

    public void destroyComponent(String type, String componentId) throws Exception {
        LOGGER.info("");
        LOGGER.info("Destroying component with type '{}' and id '{}'", type, componentId);
        ComponentFactory<Object> componentFactory = cfr.getComponentFactory(type);
        if (componentFactory != null) {
            LOGGER.info("...using ComponentFactory {}", componentFactory);
        } else {
            LOGGER.error("...no ComponentFactory associated with type '{}' in registry", type);
        }
        Class<Object> componentType = cfr.getComponentType(type);
        LOGGER.info("...component type {}", componentType);
        componentFactory.destroy(componentId, componentType);
        LOGGER.info("destroyed");
    }

    @SuppressWarnings("unchecked")
    public <COMPONENT> COMPONENT getComponent(String id) {
        ComponentHandle<?> componentHandle = componentHandleById.get(id);
        if (componentHandle != null) {
            return (COMPONENT)componentHandle.component();
        }
        return null;
    }

    public Collection<String> getComponentIds() {
        return componentHandleById.keySet();
    }

    public List<ComponentInfo> getComponentInfoList() {
        List<ComponentInfo> componentInfoList = new ArrayList<>();
        for (ComponentHandle<?> componentHandle : componentHandleById.values()) {
            componentInfoList.add(getComponentInfo(componentHandle.config().getId()));
        }
        return componentInfoList;
    }

    public ComponentInfo getComponentInfo(String id) {
        ComponentHandle<?> componentHandle = componentHandleById.get(id);
        if (componentHandle == null) {
            return null;
        }

        ComponentInfo componentInfo = new ComponentInfo();
        componentInfo.setId(id);
        componentInfo.setControlType(componentHandle.config().getType());
        componentInfo.setComponentType(componentHandle.config().getClass().getName());
        componentInfo.setConfig(componentHandle.maskedConfig());

        return componentInfo;
    }

}
