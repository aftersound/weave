package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry that keeps track of each data client and its identifier. Within
 * same registry, each identifier are expected to be unique.
 * Note this is not thread safe since it's not expected to see data clients
 * get registered/unregistered by different entities in different threads.
 */
public class DataClientRegistry {

    private final DataClientFactoryRegistry dcfr;
    private final Map<String, ClientHandle<?>> clientHandleById = new HashMap<>();
    private final Map<String, ClientHandle<?>> clientHandleBySignature = new HashMap<>();

    public DataClientRegistry(ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings) {
        try {
            this.dcfr = new DataClientFactoryRegistry(this, dataClientFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> CLIENT registerClient(String id, CLIENT client) {
        if (id == null || client == null) {
            return null;
        }

        ClientHandle existingClientHandle = clientHandleById.put(id, ClientHandle.of(client));

        // return existing if there is
        if (existingClientHandle != null) {
            return (CLIENT)existingClientHandle.client();
        } else {
            return null;
        }
    }

    <CLIENT> CLIENT unregisterClient(String id, Class<CLIENT> type) {
        ClientHandle existingClientHandle = clientHandleById.remove(id);
        if (existingClientHandle != null) {
            return type.cast(existingClientHandle.client());
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> CLIENT getClient(String id) {
        ClientHandle clientHandle = clientHandleById.get(id);
        if (clientHandle != null) {
            return (CLIENT)clientHandle.client();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> CLIENT getClient(Signature signature) {
        // TODO
        ClientHandle clientHandle = clientHandleBySignature.get(signature);
        if (clientHandle != null) {
            return (CLIENT)clientHandle.client();
        }
        return null;
    }

    public void initializeClient(String type, String id, Map<String, Object> options) throws Exception {
        if (getClient(id) == null) {
            dcfr.getDataClientFactory(type).create(id, options);
        }
    }

}
