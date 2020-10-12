package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public abstract class ComponentFactory<COMPONENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentFactory.class);

    protected final ComponentRegistry componentRegistry;

    private final Object lock = new Object();

    protected ComponentFactory(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    public final COMPONENT create(ComponentConfig componentConfig) {
        synchronized (lock) {

            // if the component with specified id and from exactly same options already exists
            ComponentHandle<COMPONENT> componentHandle = componentRegistry.getComponentHandle(componentConfig.getId());
            if (componentHandle != null && componentHandle.optionsHash() == componentConfig.getOptions().hashCode()) {
                return componentHandle.component();
            }

            Collection<Key<?>> configKeys = ComponentConfigKeysRegistry.INSTANCE.getConfigKeys(componentConfig.getType());

            // if this component factory is allowed to recreate component with options slightly different
            if (!ComponentSingletonOnly.class.isInstance(this)) {
                Config config = Config.from(componentConfig.getOptions(), configKeys);
                COMPONENT component = createComponent(config);
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, componentConfig, configKeys);
                if (existingHandle != null) {
                    destroyComponent(existingHandle.component());
                }
                return component;
            }

            // component needs to be singleton only
            ComponentSingletonOnly<Signature> singletonOnly = ComponentSingletonOnly.class.cast(this);
            Signature signature = singletonOnly.getSignature(componentConfig.getOptions());
            SignatureGroup signatureGroup = componentRegistry.matchSignatureGroup(this.getClass(), signature);
            if (signatureGroup == null) {
                COMPONENT component = createComponent(Config.from(componentConfig.getOptions(), configKeys));
                ComponentHandle<COMPONENT> existingHandle = componentRegistry.registerComponent(component, componentConfig, configKeys);

                if (existingHandle != null) {
                    componentRegistry.removeSignatureGroup(this.getClass(), componentConfig.getId());
                    destroyComponent(existingHandle.component());
                }

                signatureGroup = new SignatureGroup(componentConfig.getId());
                signatureGroup.addSignature(signature);
                componentRegistry.addSignatureGroup(this.getClass(), signatureGroup);

                return component;
            } else {
                if (signatureGroup.id().equals(componentConfig.getId())) {
                    return componentRegistry.getComponent(componentConfig.getId());
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
     * Create a COMPONENT with given config
     * @param config
     *          - {@link Config} needed by this {@link ComponentFactory} to create and get hold of an instance
     *            of COMPONENT
     * @return
     *          a COMPONENT instance
     */
    protected abstract COMPONENT createComponent(Config config);
    protected abstract void destroyComponent(COMPONENT component);
}
