package io.aftersound.weave.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ComponentFactory<COMPONENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentFactory.class);

    protected final ComponentRegistry componentRegistry;

    private final Object lock = new Object();

    protected ComponentFactory(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    public final COMPONENT create(ComponentConfig componentConfig) {
        synchronized (lock) {

            // get signature of given component config
            Signature signature = getSignatureExtractor().extract(componentConfig);

            // if the component with specified id and config already exists
            ComponentHandle<COMPONENT> componentHandle = componentRegistry.getComponentHandle(componentConfig.getId());
            if (componentHandle != null && componentHandle.configSignature().match(signature)) {
                return componentHandle.component();
            }

            // if this component factory is allowed to recreate component with same or slightly different config
            if (!ComponentSingletonOnly.class.isInstance(this)) {
                COMPONENT component = createComponent(componentConfig);
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, componentConfig, signature);
                if (existingHandle != null) {
                    destroyComponent(existingHandle.component());
                }
                return component;
            }

            // component needs to be singleton only
            final String componentFactoryType = this.getClass().getName();

            SignatureGroup signatureGroup = componentRegistry.getSignatureGroup(componentFactoryType);

            // null SignatureGroup indicates no component of given type exists yet
            if (signatureGroup == null) {
                COMPONENT component = createComponent(componentConfig);
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, componentConfig, signature);

                // not expected to see existing one, have following block just to play safe
                if (existingHandle != null) {
                    destroyComponent(existingHandle.component());
                }

                signatureGroup = new SignatureGroup(componentFactoryType);
                componentRegistry.addSignatureGroup(componentFactoryType, signatureGroup);
                signatureGroup.addSignature(componentConfig.getId(), signature);

                return component;
            } else {
                String componentId = signatureGroup.getComponentIdWithMatchingSignature(signature);
                if (componentId != null) {
                    LOGGER.info(
                            "Another component instance with id {} is created from config with the same/similar signature",
                            componentId
                    );
                    return componentRegistry.getComponent(componentConfig.getId());
                } else {
                    COMPONENT component = createComponent(componentConfig);
                    ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, componentConfig, signature);

                    if (existingHandle != null) {
                        destroyComponent(existingHandle.component());
                    }

                    signatureGroup.addSignature(componentConfig.getId(), signature);

                    return component;
                }
            }
        }
    }

    public final void destroy(String id, Class<COMPONENT> componentType) {
        synchronized (lock) {
            COMPONENT component = componentRegistry.unregisterComponent(id, componentType);
            if (component != null) {
                destroyComponent(component);
            }

            if (ComponentSingletonOnly.class.isInstance(this)) {
                SignatureGroup signatureGroup = componentRegistry.getSignatureGroup(this.getClass().getName());
                if (signatureGroup != null) {
                    signatureGroup.removeSignature(id);
                }
            }
        }
    }

    /**
     * Override this whenever necessary
     * @return {@link SignatureExtractor} paired with this factory
     */
    protected SignatureExtractor getSignatureExtractor() {
        return DefaultSignatureExtractor.INSTANCE;
    }

    /**
     * Create a COMPONENT with given config
     *
     * @param config specified configuration for this component factory to create a component
     * @return a COMPONENT instance that honors specified configuration
     */
    protected abstract COMPONENT createComponent(ComponentConfig config);

    protected abstract void destroyComponent(COMPONENT component);
}
