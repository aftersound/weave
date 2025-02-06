package io.aftersound.weave.component;

import io.aftersound.config.Config;
import io.aftersound.util.Key;

import java.util.Collection;

public abstract class SimpleComponentFactory<COMPONENT> extends ComponentFactory<COMPONENT> {

    private final SignatureExtractor signatureExtractor = new SimpleSignatureExtractor();

    protected SimpleComponentFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected SignatureExtractor getSignatureExtractor() {
        return signatureExtractor;
    }

    @Override
    protected final COMPONENT createComponent(ComponentConfig config) {
        SimpleComponentConfig scc = (SimpleComponentConfig) config;
        Collection<Key<?>> configKeys = ComponentConfigKeysRegistry.INSTANCE.getConfigKeys(scc.getType());
        return createComponent(scc.configKeys(configKeys).config());
    }

    @Override
    protected final void destroyComponent(COMPONENT component, ComponentConfig config) {
        SimpleComponentConfig scc = (SimpleComponentConfig) config;
        destroyComponent(component, scc.config());
    }


    /**
     * Create a COMPONENT with given {@link Config}
     *
     * @param config the {@link Config} needed by this {@link ComponentFactory} to create and get hold of an instance
     *               of COMPONENT
     * @return a COMPONENT instance
     */
    protected abstract COMPONENT createComponent(Config config);

    /**
     * Destroy specified component with the {@link Config} which was used to create it
     *
     * @param component the component to be destroyed
     * @param config    the {@link Config} which was used to create the specified component
     */
    protected abstract void destroyComponent(COMPONENT component, Config config);

}
