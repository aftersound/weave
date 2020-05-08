package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ClientFactoryTest {

    @Test
    public void testCreateDestroy() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();
        Map<String, String> options = new HashMap<>();

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        ClientFactory<MyDBClient> dcf = new MyDBClientFactory(dcr);
        dcf.create(Endpoint.of("MyDB", "test", options));
        MyDBClient myDbClient = dcr.getClient("test");
        assertNotNull(myDbClient);

        options.put("o1", "o1v1");
        dcf.create(Endpoint.of("MyDB", "test", options));
        // dcr now have a different instance of MyDBClient with id "test"
        assertNotSame(myDbClient, dcr.getClient("test"));

        dcf.destroy("no_matching", MyDBClient.class);
        assertNotNull(dcr.getClient("test"));

        dcf.destroy("test", MyDBClient.class);
        assertNull(dcr.getClient("test"));
    }

}