package io.aftersound.weave.component;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;

public class MyDBClientFactory extends ComponentFactory<MyDBClient> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("MyDB", ComponentConfig.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected MyDBClient createComponent(ComponentConfig endpoint) {
        return new MyDBClient();
    }

    @Override
    protected void destroyComponent(MyDBClient myDBClient) {
        // Do nothing
    }
}
