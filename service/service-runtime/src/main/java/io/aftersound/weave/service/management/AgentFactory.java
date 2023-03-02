package io.aftersound.weave.service.management;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.*;
import io.aftersound.weave.config.Config;
import io.aftersound.weave.service.ServiceInstance;

import static io.aftersound.weave.service.management.AgentConfigDictionary.KEYS;

public class AgentFactory extends SimpleComponentFactory<Agent> {

    public static final NamedType<ComponentConfig> COMPANION_CONTROL_TYPE = NamedType.of("ManagementAgent", SimpleComponentConfig.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("ManagementAgent", String.class);

    static {
        ComponentConfigKeysRegistry.INSTANCE.registerConfigKeys(COMPANION_CONTROL_TYPE.name(), KEYS);
    }

    public AgentFactory(ComponentRegistry componentRegistry) {
        super(componentRegistry);
    }

    @Override
    protected Agent createComponent(Config config) {
        final ServiceInstance serviceInstance = componentRegistry.getComponent(ServiceInstance.class.getSimpleName());
        return new Agent(config, serviceInstance);
    }

    @Override
    protected void destroyComponent(Agent agent, Config config) {
        agent.stop();
    }

}
