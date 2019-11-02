package io.aftersound.weave.dataclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class DataClientFactory<CLIENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClientFactory.class);

    protected final DataClientRegistry dataClientRegistry;

    private Object lock = new Object();

    protected DataClientFactory(DataClientRegistry dataClientRegistry) {
        this.dataClientRegistry = dataClientRegistry;
    }

    public final CLIENT create(String id, Map<String, Object> options) {
        synchronized (lock) {
            // if data client with specified id and from exactly same options already exists
            ClientHandle<CLIENT> clientHandle = dataClientRegistry.getClientHandle(id);
            if (clientHandle != null && clientHandle.optionsHash() == options.hashCode()) {
                return clientHandle.client();
            }

            // if data client is not singleton only for target database
            if (!DataClientSingletonOnly.class.isInstance(this)) {
                CLIENT client = createDataClient(options);
                ClientHandle<CLIENT> existingHandle = dataClientRegistry.registerClient(id, options, client);
                if (existingHandle != null) {
                    destroyDataClient(existingHandle.client());
                }
                return client;
            }

            // data client needs to be singleton only for target database
            DataClientSingletonOnly<Signature> singletonOnly = DataClientSingletonOnly.class.cast(this);
            Signature signature = singletonOnly.getSignature(options);
            SignatureGroup signatureGroup = dataClientRegistry.matchSignatureGroup(this.getClass(), signature);
            // no data client for target database yet
            if (signatureGroup == null) {
                CLIENT client = createDataClient(options);
                ClientHandle<CLIENT> existingHandle = dataClientRegistry.registerClient(id, options, client);

                if (existingHandle != null) {
                    dataClientRegistry.removeSignatureGroup(this.getClass(), id);
                    destroyDataClient(existingHandle.client());
                }

                signatureGroup = new SignatureGroup(id);
                signatureGroup.addSignature(signature);
                dataClientRegistry.addSignatureGroup(this.getClass(), signatureGroup);

                return client;
            } else {
                if (signatureGroup.id().equals(id)) {
                    return dataClientRegistry.getClient(id);
                } else {
                    LOGGER.error("Data client instance with id {} already connects to target database", id);
                    return null;
                }
            }
        }
    }

    public final void destroy(String id, Class<CLIENT> clientType) {
        synchronized (lock) {
            CLIENT client = dataClientRegistry.unregisterClient(id, clientType);
            if (client != null) {
                destroyDataClient(client);
            }

            if (DataClientSingletonOnly.class.isInstance(this)) {
                dataClientRegistry.removeSignatureGroup(this.getClass(), id);
            }
        }
    }

    /**
     * Create a data client with given options
     * @param options
     *          - options used by this {@link DataClientFactory} to get hold of data client instance
     * @return
     *          a data client instance
     */
    protected abstract CLIENT createDataClient(Map<String, Object> options);
    protected abstract void destroyDataClient(CLIENT client);
}
