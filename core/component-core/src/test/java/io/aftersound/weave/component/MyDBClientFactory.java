package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.config.Config;

import java.util.Collection;
import java.util.Collections;

public class MyDBClientFactory extends ComponentFactory<MyDBClient> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("MyDB", ComponentConfig.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected Collection<Key<?>> getComponentConfigKeys() {
        return Collections.emptyList();
    }

    @Override
    protected MyDBClient createComponent(Config config) {
        return new MyDBClient();
    }

    @Override
    protected void destroyComponent(MyDBClient myDBClient) {
        // Do nothing
    }
}
