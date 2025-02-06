package io.aftersound.weave.hsqldb;

import io.aftersound.config.Config;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.*;
import org.hsqldb.Server;

import static io.aftersound.weave.hsqldb.HSQLDBConfigDictionary.KEYS;

public class HSQLDBFactory extends SimpleComponentFactory<Server> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("HSQLDB", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("HSQLDB", Server.class);

    static {
        ComponentConfigKeysRegistry.INSTANCE.registerConfigKeys(COMPANION_CONTROL_TYPE.name(), KEYS);
    }

    public HSQLDBFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected Server createComponent(Config config) {
        try {
            Server server = new Server();
            server.setProperties(config.asProperties());
            server.start();
            return server;
        } catch (Exception e) {
            throw new ComponentCreateException("failed to create and start server", e);
        }
    }

    @Override
    protected void destroyComponent(Server server, Config config) {
        server.stop();
    }

}
