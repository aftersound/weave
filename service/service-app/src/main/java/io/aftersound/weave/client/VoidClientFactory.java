package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentConfigKeysRegistry;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.SimpleComponentFactory;
import io.aftersound.weave.config.Config;

import java.util.Collections;

public class VoidClientFactory extends SimpleComponentFactory<Object> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of(
            "VOID",
            ComponentConfig.class
    );

    static {
        ComponentConfigKeysRegistry.INSTANCE.registerConfigKeys(COMPANION_CONTROL_TYPE.name(), Collections.emptyList());
    }

    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("VOID", Object.class);

    private static final Object VOID = new Object();

    public VoidClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected Object createComponent(Config config) {
        return VOID;
    }

    @Override
    protected void destroyComponent(Object obj) {
    }
}
