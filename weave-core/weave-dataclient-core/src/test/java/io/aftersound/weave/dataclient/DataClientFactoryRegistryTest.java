package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class DataClientFactoryRegistryTest {

    @Test
    public void unregisterDataClientFactory() throws Exception {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        dcfBindings.register(
                MyDBClientFactory.COMPANINON_CONTROL_TYPE,
                MyDBClientFactory.class,
                MyDBClientFactory.COMPANINON_PRODUCT_TYPE
        );
        dcfBindings.register(
                MyDBClientFactory2.COMPANINON_CONTROL_TYPE,
                MyDBClientFactory2.class,
                MyDBClientFactory2.COMPANINON_PRODUCT_TYPE
        );

        DataClientRegistry dcr = new DataClientRegistry(dcfBindings);
        DataClientFactoryRegistry dcfr = new DataClientFactoryRegistry(dcr, dcfBindings);
        dcfr.unregisterDataClientFactory(MyDBClient.class);
    }

    @Test
    public void getDataClientFactory() throws Exception {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        dcfBindings.register(
                MyDBClientFactory.COMPANINON_CONTROL_TYPE,
                MyDBClientFactory.class,
                MyDBClientFactory.COMPANINON_PRODUCT_TYPE
        );
        dcfBindings.register(
                MyDBClientFactory2.COMPANINON_CONTROL_TYPE,
                MyDBClientFactory2.class,
                MyDBClientFactory2.COMPANINON_PRODUCT_TYPE
        );

        DataClientRegistry dcr = new DataClientRegistry(dcfBindings);
        DataClientFactoryRegistry dcfr = new DataClientFactoryRegistry(dcr, dcfBindings).initialize();
        DataClientFactory<MyDBClient> dcf = dcfr.getDataClientFactory(MyDBClient.class);
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