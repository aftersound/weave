package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;
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

    public void testCreateDestroy1() throws Exception {
        ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings =
                ActorBindingsUtil.loadActorBindings(
                        Arrays.asList(
                                "io.aftersound.weave.client.MyDBClientFactory",
                                "io.aftersound.weave.client.MyDB2ClientFactory"
                        ),
                        Endpoint.class,
                        Object.class,
                        false
                );

        ActorRegistry<ClientFactory<?>> clientFactoryRegistry = new ActorFactory<>(clientFactoryBindings)
                .createActorRegistryFromBindings(false);

        Endpoint endpoint = Endpoint.of(
                "MyDB",
                "mydb.test.client",
                MapBuilder.<String, String>hashMap().build()
        );
        Object client = clientFactoryRegistry.get(endpoint.getType()).create(endpoint);
        assertTrue(client instanceof MyDBClient);

        endpoint = Endpoint.of(
                "MyDB2",
                "mydb.test.client",
                MapBuilder.<String, String>hashMap().build()
        );
        client = clientFactoryRegistry.get(endpoint.getType()).create(endpoint);
        assertTrue(client instanceof MyDB2Client);
    }

}