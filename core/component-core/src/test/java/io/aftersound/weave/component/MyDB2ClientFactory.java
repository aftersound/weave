package io.aftersound.weave.component;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.config.Config;

public class MyDB2ClientFactory extends SimpleComponentFactory<MyDB2Client> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("MyDB2", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB2", MyDB2Client.class);

    MyDB2ClientFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected MyDB2Client createComponent(Config config) {
        return new MyDB2Client();
    }

    @Override
    protected void destroyComponent(MyDB2Client myDBClient) {
        // Do nothing
    }
}
