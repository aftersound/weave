package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.config.Config;

import java.util.Collection;

public abstract class SimpleComponentFactory<COMPONENT> extends ComponentFactory<COMPONENT> {

    protected SimpleComponentFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected final COMPONENT createComponent(ComponentConfig config) {
        SimpleComponentConfig scc = (SimpleComponentConfig) config;
        Collection<Key<?>> configKeys = ComponentConfigKeysRegistry.INSTANCE.getConfigKeys(scc.getType());
        Config cfg = Config.from(scc.getOptions(), configKeys);
        return createComponent(cfg);
    }

    /**
     * Create a COMPONENT with given config
     *
     * @param config - {@link Config} needed by this {@link ComponentFactory} to create and get hold of an instance
     *               of COMPONENT
     * @return a COMPONENT instance
     */
    protected abstract COMPONENT createComponent(Config config);

}
