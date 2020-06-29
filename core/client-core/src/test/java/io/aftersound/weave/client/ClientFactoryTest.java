package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ClientFactoryTest {

    @Test
    public void testCreateDestroy() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> cfBindings = new ActorBindings<>();
        Map<String, String> options = new HashMap<>();

        ClientRegistry cr = new ClientRegistry(cfBindings);
        ClientFactory<MyDBClient> cf = new MyDBClientFactory(cr);
        cf.create(Endpoint.of("MyDB", "test", options));
        MyDBClient myDbClient = cr.getClient("test");
        assertNotNull(myDbClient);

        options = new HashMap<>();
        options.put("o1", "o1v1");
        cf.create(Endpoint.of("MyDB", "test", options));
        // cr now have a different instance of MyDBClient with id "test"
        assertNotSame(myDbClient, cr.getClient("test"));

        cf.destroy("no_matching", MyDBClient.class);
        assertNotNull(cr.getClient("test"));

        cf.destroy("test", MyDBClient.class);
        assertNull(cr.getClient("test"));
    }

}