package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maintains {@link ClientFactory} for known clients.
 */
public class ClientFactoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryRegistry.class);

    private final Object lock = new Object();

    private final ClientRegistry clientRegistry;
    private final ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings;

    private Map<Class<?>, ClientFactory<?>> factoryByClientType = new HashMap<>();

    ClientFactoryRegistry(
            ClientRegistry clientRegistry,
            ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings) {
        this.clientRegistry = clientRegistry;
        this.clientFactoryBindings = clientFactoryBindings;
    }

    ClientFactoryRegistry initialize() throws Exception {
        synchronized (lock) {
            NamedTypes<Endpoint> controlTypes = clientFactoryBindings.controlTypes();
            NamedTypes<Object> clientTypes = clientFactoryBindings.productTypes();
            for (String name : controlTypes.names()) {
                Class<?> clientType = clientTypes.get(name).type();
                Class<?> factoryType = clientFactoryBindings.getActorType(name);
                ClientFactory<?> factory = createFactory0(factoryType);
                factoryByClientType.put(clientType, factory);
            }
        }
        return this;
    }

    public <CLIENT> void unregisterClientFactory(Class<CLIENT> clientType) {
        synchronized (lock) {
            factoryByClientType.remove(clientType);
        }
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> ClientFactory<CLIENT> getClientFactory(Class<CLIENT> clientType) throws Exception {
        return (ClientFactory<CLIENT>) factoryByClientType.get(clientType);
    }

    private ClientFactory<?> createFactory0(Class<?> factoryType) throws Exception {
        try {
            Constructor<? extends ClientFactory<?>> constructor =
                    (Constructor<? extends ClientFactory<?>>)factoryType.getDeclaredConstructor(ClientRegistry.class);
            return constructor.newInstance(clientRegistry);
        } catch (Exception any) {
            LOGGER.error("failed to instantiate an instance of " + factoryType, any);
            throw any;
        }
    }

    public <CLIENT, FACTORY extends ClientFactory<CLIENT>> FACTORY getClientFactory(String name) throws Exception {
        Class<CLIENT> clientType = (Class<CLIENT>) clientFactoryBindings.getProductTypeByName(name);
        return (FACTORY) getClientFactory(clientType);
    }

    public Class<Object> getClientType(String name) {
        return clientFactoryBindings.getProductTypeByName(name);
    }

}
