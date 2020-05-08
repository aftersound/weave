package io.aftersound.weave.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientFactory<CLIENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactory.class);

    protected final ClientRegistry dataClientRegistry;

    private Object lock = new Object();

    protected ClientFactory(ClientRegistry dataClientRegistry) {
        this.dataClientRegistry = dataClientRegistry;
    }

    public final CLIENT create(Endpoint endpoint) {
        synchronized (lock) {
            // if data client with specified id and from exactly same options already exists
            ClientHandle<CLIENT> clientHandle = dataClientRegistry.getClientHandle(endpoint.getId());
            if (clientHandle != null && clientHandle.optionsHash() == endpoint.getOptions().hashCode()) {
                return clientHandle.client();
            }

            // if data client is not singleton only for target database
            if (!ClientSingletonOnly.class.isInstance(this)) {
                CLIENT client = createDataClient(endpoint);
                ClientHandle<CLIENT> existingHandle = dataClientRegistry.registerClient(
                        endpoint.getId(),
                        endpoint.getOptions(),
                        client
                );
                if (existingHandle != null) {
                    destroyDataClient(existingHandle.client());
                }
                return client;
            }

            // data client needs to be singleton only for target database
            ClientSingletonOnly<Signature> singletonOnly = ClientSingletonOnly.class.cast(this);
            Signature signature = singletonOnly.getSignature(endpoint.getOptions());
            SignatureGroup signatureGroup = dataClientRegistry.matchSignatureGroup(this.getClass(), signature);
            // no data client for target database yet
            if (signatureGroup == null) {
                CLIENT client = createDataClient(endpoint);
                ClientHandle<CLIENT> existingHandle = dataClientRegistry.registerClient(
                        endpoint.getId(),
                        endpoint.getOptions(),
                        client
                );

                if (existingHandle != null) {
                    dataClientRegistry.removeSignatureGroup(this.getClass(), endpoint.getId());
                    destroyDataClient(existingHandle.client());
                }

                signatureGroup = new SignatureGroup(endpoint.getId());
                signatureGroup.addSignature(signature);
                dataClientRegistry.addSignatureGroup(this.getClass(), signatureGroup);

                return client;
            } else {
                if (signatureGroup.id().equals(endpoint.getId())) {
                    return dataClientRegistry.getClient(endpoint.getId());
                } else {
                    LOGGER.error(
                            "Another client instance with id {} already connects to target database that allows only 1 client instance",
                            signatureGroup.id()
                    );
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

            if (ClientSingletonOnly.class.isInstance(this)) {
                dataClientRegistry.removeSignatureGroup(this.getClass(), id);
            }
        }
    }

    /**
     * Create a data client with given options
     * @param endpoint
     *          - {@link Endpoint} needed by this {@link ClientFactory} to create and get hold of data client instance
     * @return
     *          a data client instance
     */
    protected abstract CLIENT createDataClient(Endpoint endpoint);
    protected abstract void destroyDataClient(CLIENT client);
}
