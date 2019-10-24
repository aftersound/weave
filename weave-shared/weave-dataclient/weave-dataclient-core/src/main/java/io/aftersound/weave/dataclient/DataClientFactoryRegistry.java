package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maintains {@link DataClientFactory} for known data clients.
 */
public class DataClientFactoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClientFactoryRegistry.class);

    private final Object lock = new Object();

    private final DataClientRegistry dataClientRegistry;
    private final ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings;

    private Map<Class<?>, DataClientFactory<?>> factoryByClientType = new HashMap<>();

    DataClientFactoryRegistry(
            DataClientRegistry dataClientRegistry,
            ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings) {
        this.dataClientRegistry = dataClientRegistry;
        this.dataClientFactoryBindings = dataClientFactoryBindings;
    }

    DataClientFactoryRegistry initialize() throws Exception {
        synchronized (lock) {
            NamedTypes<Endpoint> controlTypes = dataClientFactoryBindings.controlTypes();
            NamedTypes<Object> clientTypes = dataClientFactoryBindings.productTypes();
            for (String name : controlTypes.names()) {
                Class<?> clientType = clientTypes.get(name).type();
                Class<?> factoryType = dataClientFactoryBindings.getActorType(name);
                DataClientFactory<?> factory = createFactory0(factoryType);
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
    public <CLIENT> DataClientFactory<CLIENT> getDataClientFactory(Class<CLIENT> clientType) throws Exception {
        return (DataClientFactory<CLIENT>) factoryByClientType.get(clientType);
    }

    private DataClientFactory<?> createFactory0(Class<?> factoryType) throws Exception {
        try {
            Constructor<? extends DataClientFactory<?>> constructor =
                    (Constructor<? extends DataClientFactory<?>>)factoryType.getDeclaredConstructor(DataClientRegistry.class);
            return constructor.newInstance(dataClientRegistry);
        } catch (Exception any) {
            LOGGER.error("failed to instantiate an instance of " + factoryType, any);
            throw any;
        }
    }

    public <CLIENT, FACTORY extends DataClientFactory<CLIENT>> FACTORY getDataClientFactory(String name) throws Exception {
        Class<CLIENT> clientType = (Class<CLIENT>) dataClientFactoryBindings.getProductTypeByName(name);
        return (FACTORY) getDataClientFactory(clientType);
    }

}
