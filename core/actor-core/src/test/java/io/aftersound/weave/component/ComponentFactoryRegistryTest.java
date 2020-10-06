package io.aftersound.weave.component;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ComponentFactoryRegistryTest {

    @Test
    public void unregisterDataClientFactory() throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> cfBindings = new ActorBindings<>();

        cfBindings.register(
                MyDBClientFactory.COMPANION_CONTROL_TYPE,
                MyDBClientFactory.class,
                MyDBClientFactory.COMPANINON_PRODUCT_TYPE
        );
        cfBindings.register(
                MyDB2ClientFactory.COMPANION_CONTROL_TYPE,
                MyDB2ClientFactory.class,
                MyDB2ClientFactory.COMPANINON_PRODUCT_TYPE
        );

        ComponentRegistry cr = new ComponentRegistry(cfBindings);
        ComponentFactoryRegistry cfr = new ComponentFactoryRegistry(cr, cfBindings);
        cfr.unregisterComponentFactory(MyDBClient.class);
    }

    @Test
    public void getDataClientFactory() throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> dcfBindings = new ActorBindings<>();

        dcfBindings.register(
                MyDBClientFactory.COMPANION_CONTROL_TYPE,
                MyDBClientFactory.class,
                MyDBClientFactory.COMPANINON_PRODUCT_TYPE
        );
        dcfBindings.register(
                MyDB2ClientFactory.COMPANION_CONTROL_TYPE,
                MyDB2ClientFactory.class,
                MyDB2ClientFactory.COMPANINON_PRODUCT_TYPE
        );

        ComponentRegistry cr = new ComponentRegistry(dcfBindings);
        ComponentFactoryRegistry cfr = new ComponentFactoryRegistry(cr, dcfBindings).initialize();
        ComponentFactory<MyDBClient> cf = cfr.getComponentFactory(MyDBClient.class);
        assertNotNull(cf);

        ComponentConfig endpoint = ComponentConfig.of(
                "MyDB",
                "test",
                new HashMap<String, String>()
        );
        MyDBClient mydbClient = cf.create(endpoint);

        assertSame(mydbClient, cr.getComponent("test"));

    }
}