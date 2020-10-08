package io.aftersound.weave.component;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.Key;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ComponentRegistryTest {

    @Test
    public void registerComponent() {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ComponentRegistry cr = new ComponentRegistry(dcfBindings);
        assertNull(
                cr.registerComponent(
                        new MyDBClient(),
                        null,
                        Collections.<Key<?>>emptyList()
                )
        );
        assertNull(
                cr.registerComponent(
                        new MyDBClient(),
                        ComponentConfig.of(null, "test", Collections.<String, String>emptyMap()),
                        Collections.<Key<?>>emptyList()
                )
        );
        assertNull(
                cr.registerComponent(
                        new MyDBClient(),
                        ComponentConfig.of("test", null, Collections.<String, String>emptyMap()),
                        Collections.<Key<?>>emptyList()
                )
        );
        assertNull(
                cr.registerComponent(
                        new ComponentConfig(),
                        ComponentConfig.of("test", "test", null),
                        Collections.<Key<?>>emptyList()
                )
        );
        assertNull(
                cr.registerComponent(
                        null,
                        ComponentConfig.of("test", "test", Collections.<String, String>emptyMap()),
                        Collections.<Key<?>>emptyList()
                )
        );
        assertNotNull(
                cr.registerComponent(
                        new MyDBClient(),
                        ComponentConfig.of("test", "test", Collections.<String, String>emptyMap()),
                        Collections.<Key<?>>emptyList()
                )
        );
    }

    @Test
    public void unregisterClient() {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> dcfBindings = new ActorBindings<>();

        ComponentRegistry cr = new ComponentRegistry(dcfBindings);
        MyDBClient unregistered = cr.unregisterComponent("test", MyDBClient.class);
        assertNull(unregistered);

        cr.registerComponent(
                new MyDBClient(),
                ComponentConfig.of("test", "test", Collections.<String, String>emptyMap()),
                Collections.<Key<?>>emptyList()
        );

        unregistered = cr.unregisterComponent("test", MyDBClient.class);
        assertNotNull(unregistered);

        unregistered = cr.unregisterComponent("test", MyDBClient.class);
        assertNull(unregistered);
    }

    @Test
    public void getClient() {
    }
}