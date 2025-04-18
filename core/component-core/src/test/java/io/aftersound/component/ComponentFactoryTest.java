package io.aftersound.component;

import io.aftersound.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ComponentFactoryTest {

    @Test
    public void testCreateDestroy() {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> cfBindings = new ActorBindings<>();
        Map<String, Object> options = new HashMap<>();

        ComponentRegistry cr = new ComponentRegistry(cfBindings);
        ComponentFactory<MyDBClient> cf = new MyDBClientFactory(cr);
        cf.create(SimpleComponentConfig.of("MyDB", "test", options));
        MyDBClient myDbClient = cr.getComponent("test");
        assertNotNull(myDbClient);

        options = new HashMap<>();
        options.put("o1", "o1v1");
        cf.create(SimpleComponentConfig.of("MyDB", "test", options));
        // cr now have a different instance of MyDBClient with id "test"
        assertNotSame(myDbClient, cr.<MyDBClient>getComponent("test"));

        cf.destroy("no_matching", MyDBClient.class);
        assertNotNull(cr.getComponent("test"));

        cf.destroy("test", MyDBClient.class);
        assertNull(cr.getComponent("test"));
    }

}