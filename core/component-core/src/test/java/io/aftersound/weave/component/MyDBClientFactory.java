package io.aftersound.weave.component;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.config.Config;

public class MyDBClientFactory extends SimpleComponentFactory<MyDBClient> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("MyDB", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected MyDBClient createComponent(Config config) {
        return new MyDBClient();
    }

    @Override
    protected void destroyComponent(MyDBClient myDBClient, Config config) {
        // Do nothing
    }
}
