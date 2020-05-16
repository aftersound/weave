package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maintains {@link ClientFactory} for known data clients.
 */
public class ClientFactoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryRegistry.class);

    private final Object lock = new Object();

    private final ClientRegistry dataClientRegistry;
    private final ActorBindings<Endpoint, ClientFactory<?>, Object> dataClientFactoryBindings;

    private Map<Class<?>, ClientFactory<?>> factoryByClientType = new HashMap<>();

    ClientFactoryRegistry(
            ClientRegistry dataClientRegistry,
            ActorBindings<Endpoint, ClientFactory<?>, Object> dataClientFactoryBindings) {
        this.dataClientRegistry = dataClientRegistry;
        this.dataClientFactoryBindings = dataClientFactoryBindings;
    }

    ClientFactoryRegistry initialize() throws Exception {
        synchronized (lock) {
            NamedTypes<Endpoint> controlTypes = dataClientFactoryBindings.controlTypes();
            NamedTypes<Object> clientTypes = dataClientFactoryBindings.productTypes();
            for (String name : controlTypes.names()) {
                Class<?> clientType = clientTypes.get(name).type();
                Class<?> factoryType = dataClientFactoryBindings.getActorType(name);
                ClientFactory<?> factory = createFactory0(factoryType);
                factoryByClientType.put(clientType, factory);
            }
        }
        return this;
    }

    public <CLIENT> void unregisterDataClientFactory(Class<CLIENT> clientType) {
        synchronized (lock) {
            factoryByClientType.remove(clientType);
        }
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> ClientFactory<CLIENT> getDataClientFactory(Class<CLIENT> clientType) throws Exception {
        return (ClientFactory<CLIENT>) factoryByClientType.get(clientType);
    }

    private ClientFactory<?> createFactory0(Class<?> factoryType) throws Exception {
        try {
            Constructor<? extends ClientFactory<?>> constructor =
                    (Constructor<? extends ClientFactory<?>>)factoryType.getDeclaredConstructor(ClientRegistry.class);
            return constructor.newInstance(dataClientRegistry);
        } catch (Exception any) {
            LOGGER.error("failed to instantiate an instance of " + factoryType, any);
            throw any;
        }
    }

    public <CLIENT, FACTORY extends ClientFactory<CLIENT>> FACTORY getDataClientFactory(String name) throws Exception {
        Class<CLIENT> clientType = (Class<CLIENT>) dataClientFactoryBindings.getProductTypeByName(name);
        return (FACTORY) getDataClientFactory(clientType);
    }

    public Class<Object> getDataClientType(String name) {
        return dataClientFactoryBindings.getProductTypeByName(name);
    }

}
