package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ClientRegistryTest {

    @Test
    public void registerClient() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        assertNull(dcr.registerClient(null, null, new MyDBClient()));
        assertNull(dcr.registerClient(null, null, new MyDBClient()));

        assertNull(dcr.registerClient("test", null, null));
        assertNull(dcr.registerClient("test", null, null));

        assertNull(dcr.registerClient("test", null, new MyDBClient()));

        assertNull(dcr.registerClient("test", new HashMap<String, String>(), new MyDBClient()));
        assertNotNull(dcr.registerClient("test", new HashMap<String, String>(), new MyDBClient()));
    }

    @Test
    public void unregisterClient() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        MyDBClient unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);

        dcr.registerClient("test", new HashMap<String, String>(), new MyDBClient());

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNotNull(unregistered);

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);
    }

    @Test
    public void getClient() {
    }
}