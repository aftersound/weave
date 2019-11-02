package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class DataClientRegistryTest {

    @Test
    public void registerClient() {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        DataClientRegistry dcr = new DataClientRegistry(dcfBindings);
        assertNull(dcr.registerClient(null, null, new MyDBClient()));
        assertNull(dcr.registerClient(null, null, new MyDBClient()));

        assertNull(dcr.registerClient("test", null, null));
        assertNull(dcr.registerClient("test", null, null));

        assertNull(dcr.registerClient("test", null, new MyDBClient()));

        assertNotNull(dcr.registerClient("test", new HashMap<String, Object>(), new MyDBClient()));
    }

    @Test
    public void unregisterClient() {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        DataClientRegistry dcr = new DataClientRegistry(dcfBindings);
        MyDBClient unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);

        dcr.registerClient("test", new HashMap<String, Object>(), new MyDBClient());

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNotNull(unregistered);

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);
    }

    @Test
    public void getClient() {
    }
}