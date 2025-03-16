package io.aftersound.component;

import io.aftersound.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ComponentFactoryRegistryTest {

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
        ComponentFactory<MyDBClient> cf = cfr.getComponentFactory("MyDB");
        assertNotNull(cf);

        ComponentConfig endpoint = SimpleComponentConfig.of(
                "MyDB",
                "test",
                new HashMap<>()
        );
        MyDBClient mydbClient = cf.create(endpoint);

        assertSame(mydbClient, cr.getComponent("test"));

    }
}