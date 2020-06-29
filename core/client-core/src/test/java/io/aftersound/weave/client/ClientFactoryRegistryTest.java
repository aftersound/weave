package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class ClientFactoryRegistryTest {

    @Test
    public void unregisterDataClientFactory() throws Exception {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

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

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        ClientFactoryRegistry dcfr = new ClientFactoryRegistry(dcr, dcfBindings);
        dcfr.unregisterClientFactory(MyDBClient.class);
    }

    @Test
    public void getDataClientFactory() throws Exception {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

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

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        ClientFactoryRegistry dcfr = new ClientFactoryRegistry(dcr, dcfBindings).initialize();
        ClientFactory<MyDBClient> dcf = dcfr.getClientFactory(MyDBClient.class);
        assertNotNull(dcf);

        Endpoint endpoint = Endpoint.of(
                "MyDB",
                "test",
                new HashMap<String, String>()
        );
        MyDBClient mydbClient = dcf.create(endpoint);

        assertSame(mydbClient, dcr.getClient("test"));

    }
}