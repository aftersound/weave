package io.aftersound.weave.service.management;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.SimpleComponentConfig;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class AgentFactoryTest {

    @Test
    public void createDestroy() throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> ab = ActorBindingsUtil.loadActorBindings(
                Arrays.asList(
                        AgentFactory.class.getName()
                ),
                ComponentConfig.class,
                Object.class,
                false
        );
        ComponentRegistry cr = new ComponentRegistry(ab);

        System.setProperty("WEAVE_EA", "true");
        System.setProperty("WEAVE_RSU", "http://localhost:8080");

        cr.initializeComponent(
                SimpleComponentConfig.of(
                        "ManagementAgent",
                        "management.agent",
                        MapBuilder.hashMap()
                                .kv("agent.enabled", "${WEAVE_AE}")
                                .kv("registry.service.uri", "${WEAVE_RSU}")
                                .build()
                )
        );

        Agent agent = cr.getComponent("management.agent");
        assertNotNull(agent);

        cr.destroyComponent("ManagementAgent", "management.agent");
    }

}