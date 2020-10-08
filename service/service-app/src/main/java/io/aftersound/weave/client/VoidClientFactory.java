package io.aftersound.weave.client;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.config.Config;

import java.util.Collection;
import java.util.Collections;

public class VoidClientFactory extends ComponentFactory<Object> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of(
            "VOID",
            ComponentConfig.class
    );

    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("VOID", Object.class);

    private static final Object VOID = new Object();

    public VoidClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected Collection<Key<?>> getComponentConfigKeys() {
        return Collections.emptyList();
    }

    @Override
    protected Object createComponent(Config config) {
        return VOID;
    }

    @Override
    protected void destroyComponent(Object obj) {
    }
}
