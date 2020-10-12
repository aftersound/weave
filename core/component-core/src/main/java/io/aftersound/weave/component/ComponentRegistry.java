package io.aftersound.weave.component;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ComponentRegistry {

    private final ComponentFactoryRegistry cfr;
    private final Map<String, ComponentHandle<?>> componentHandleById = new HashMap<>();
    private final Map<Class<? extends ComponentFactory>, List<SignatureGroup>> signatureGroupsByFactory = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentRegistry.class);

    public ComponentRegistry(ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings) {
        try {
            this.cfr = new ComponentFactoryRegistry(this, componentFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <COMPONENT> ComponentHandle<COMPONENT> registerComponent(COMPONENT component, ComponentConfig config, Collection<Key<?>> configKeys) {
        if (component == null || config == null || config.getId() == null || config.getOptions() == null) {
            return null;
        }

        return (ComponentHandle<COMPONENT>) componentHandleById.put(
                config.getId(),
                ComponentHandle.of(component, config, configKeys));
    }

    <COMPONENT> ComponentHandle<COMPONENT> getComponentHandle(String id) {
        return (ComponentHandle<COMPONENT>)componentHandleById.get(id);
    }

    <COMPONENT> COMPONENT unregisterComponent(String id, Class<COMPONENT> type) {
        ComponentHandle existingComponentHandle = componentHandleById.remove(id);
        if (existingComponentHandle != null) {
            return type.cast(existingComponentHandle.component());
        } else {
            return null;
        }
    }

    SignatureGroup matchSignatureGroup(Class<? extends ComponentFactory> cfClass, Signature signature) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(cfClass);
        if (signatureGroups == null || signatureGroups.isEmpty()) {
            return null;
        }

        for (SignatureGroup group : signatureGroups) {
            if (group.hasMatching(signature)) {
                return group;
            }
        }
        return null;
    }

    void addSignatureGroup(Class<? extends ComponentFactory> cfClass, SignatureGroup signatureGroup) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(cfClass);
        if (signatureGroups == null) {
            signatureGroups = new ArrayList<>();
            signatureGroupsByFactory.put(cfClass, signatureGroups);
        }
        signatureGroups.add(signatureGroup);
    }

    void removeSignatureGroup(Class<? extends ComponentFactory> cfClass, String id) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(cfClass);
        if (signatureGroups != null && !signatureGroups.isEmpty()) {
            int target = -1;
            for (int index = 0; index < signatureGroups.size(); index++) {
                if (signatureGroups.get(index).id().equals(id)) {
                    target = index;
                    break;
                }
            }
            if (target >= 0) {
                signatureGroups.remove(target);
            }
        }
    }

    public void initializeComponent(ComponentConfig config) throws Exception {
        LOGGER.info("");
        LOGGER.info("Creating component with type '{}' and id '{}'", config.getType(), config.getId());
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