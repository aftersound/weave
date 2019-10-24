package io.aftersound.weave.dataclient;

import java.util.Map;

public abstract class DataClientFactory<CLIENT> {

    protected final DataClientRegistry dataClientRegistry;

    private Object lock = new Object();

    protected DataClientFactory(DataClientRegistry dataClientRegistry) {
        this.dataClientRegistry = dataClientRegistry;
    }

    public final CLIENT create(String id, Map<String, Object> options) {
        synchronized (lock) {
            CLIENT client = createDataClient(options);
            CLIENT existingClient = this.dataClientRegistry.registerClient(id, client);
            if (existingClient != null) {
                destroyDataClient(existingClient);
            }
            return client;
        }
    }

    public final void destroy(String id, Class<CLIENT> clientType) {
        synchronized (lock) {
            CLIENT client = this.dataClientRegistry.unregisterClient(id, clientType);
            if (client != null) {
                destroyDataClient(client);
            }
        }
    }

    protected abstract CLIENT createDataClient(Map<String, Object> options);
    protected abstract void destroyDataClient(CLIENT client);
}
