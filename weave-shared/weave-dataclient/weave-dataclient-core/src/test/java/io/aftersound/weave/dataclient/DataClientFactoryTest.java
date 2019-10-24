package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class DataClientFactoryTest {

    @Test
    public void testCreateDestroy() {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        DataClientRegistry dcr = new DataClientRegistry(dcfBindings);
        DataClientFactory<MyDBClient> dcf = new MyDBClientFactory(dcr);
        dcf.create("test", new HashMap<String, Object>());
        MyDBClient myDbClient = dcr.getClient("test");
        assertNotNull(myDbClient);

        dcf.create("test", new HashMap<String, Object>());
        // dcr now have a different instance of MyDBClient with id "test"
        assertNotSame(myDbClient, dcr.getClient("test"));

        dcf.destroy("no_matching", MyDBClient.class);
        assertNotNull(dcr.getClient("test"));

        dcf.destroy("test", MyDBClient.class);
        assertNull(dcr.getClient("test"));
    }

}