package io.aftersound.weave.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ComponentFactory<COMPONENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentFactory.class);

    protected final ComponentRegistry componentRegistry;

    private Object lock = new Object();

    protected ComponentFactory(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    public final COMPONENT create(ComponentConfig config) {
        synchronized (lock) {
            // if the component with specified id and from exactly same options already exists
            ComponentHandle<COMPONENT> componentHandle = componentRegistry.getComponentHandle(config.getId());
            if (componentHandle != null && componentHandle.optionsHash() == config.getOptions().hashCode()) {
                return componentHandle.component();
            }

            // if this component factory is allowed to recreate component with options slightly different
            if (!ComponentSingletonOnly.class.isInstance(this)) {
                COMPONENT component = createComponent(config);
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, config);
                if (existingHandle != null) {
                    destroyComponent(existingHandle.component());
                }
                return component;
            }

            // component needs to be singleton only
            ComponentSingletonOnly<Signature> singletonOnly = ComponentSingletonOnly.class.cast(this);
            Signature signature = singletonOnly.getSignature(config.getOptions());
            SignatureGroup signatureGroup = componentRegistry.matchSignatureGroup(this.getClass(), signature);
            if (signatureGroup == null) {
                COMPONENT component = createComponent(config);
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, config);

                if (existingHandle != null) {
                    componentRegistry.removeSignatureGroup(this.getClass(), config.getId());
                    destroyComponent(existingHandle.component());
                }

                signatureGroup = new SignatureGroup(config.getId());
                signatureGroup.addSignature(signature);
                componentRegistry.addSignatureGroup(this.getClass(), signatureGroup);

                return component;
            } else {
                if (signatureGroup.id().equals(config.getId())) {
                    return componentRegistry.getComponent(config.getId());
                } else {
                    LOGGER.error(
                            "Another component instance with id {} is created from options with the same signature",
                            signatureGroup.id()
                    );
                    return null;
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
                componentRegistry.removeSignatureGroup(this.getClass(), id);
            }
        }
    }

    /**
     * Create a COMPONENT with given options
     * @param config
     *          - {@link ComponentConfig} needed by this {@link ComponentFactory} to create and get hold of an instance
     *            of COMPONENT
     * @return
     *          a COMPONENT instance
     */
    protected abstract COMPONENT createComponent(ComponentConfig config);
    protected abstract void destroyComponent(COMPONENT component);
}
