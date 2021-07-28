package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.config.Config;

import java.util.Collection;

public abstract class SimpleComponentCreator<COMPONENT> implements ComponentCreator<COMPONENT> {

    @Override
    public final COMPONENT create(ComponentConfig componentConfig) {
        SimpleComponentConfig scc = (SimpleComponentConfig) componentConfig;
        Collection<Key<?>> configKeys = ComponentConfigKeysRegistry.INSTANCE.getConfigKeys(scc.getType());
        Config cfg = Config.from(scc.getOptions(), configKeys);
        return create(cfg);
    }

    /**
     * Creates component with specified config
     *
     * @param config component config in form of {@link Config}, parsed from {@link SimpleComponentConfig}
     * @return a component created
     */
    protected abstract COMPONENT create(Config config);

}
