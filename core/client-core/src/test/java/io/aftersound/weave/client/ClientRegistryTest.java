package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClientRegistryTest {

    @Test
    public void registerClient() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        assertNull(dcr.registerClient(new MyDBClient(), null));
        assertNull(dcr.registerClient(new MyDBClient(), Endpoint.of(null, "test", Collections.<String, String>emptyMap())));
        assertNull(dcr.registerClient(new MyDBClient(), Endpoint.of("test", null, Collections.<String, String>emptyMap())));
        assertNull(dcr.registerClient(new MyDBClient(), Endpoint.of("test", "test", null)));
        assertNull(dcr.registerClient(null, Endpoint.of("test", "test", Collections.<String, String>emptyMap())));
        assertNotNull(dcr.registerClient(new MyDBClient(), Endpoint.of("test", "test", Collections.<String, String>emptyMap())));
    }

    @Test
    public void unregisterClient() {
        ActorBindings<Endpoint, ClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ClientRegistry dcr = new ClientRegistry(dcfBindings);
        MyDBClient unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);

        dcr.registerClient(new MyDBClient(), Endpoint.of("test", "test", Collections.<String, String>emptyMap()));

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNotNull(unregistered);

        unregistered = dcr.unregisterClient("test", MyDBClient.class);
        assertNull(unregistered);
    }

    @Test
    public void getClient() {
    }
}