package io.aftersound.weave.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientFactory<CLIENT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactory.class);

    protected final ClientRegistry clientRegistry;

    private Object lock = new Object();

    protected ClientFactory(ClientRegistry clientRegistry) {
        this.clientRegistry = clientRegistry;
    }

    public final CLIENT create(Endpoint endpoint) {
        synchronized (lock) {
            // if client with specified id and from exactly same options already exists
            ClientHandle<CLIENT> clientHandle = clientRegistry.getClientHandle(endpoint.getId());
            if (clientHandle != null && clientHandle.optionsHash() == endpoint.getOptions().hashCode()) {
                return clientHandle.client();
            }

            // if client is not singleton only for target server/cluster
            if (!ClientSingletonOnly.class.isInstance(this)) {
                CLIENT client = createClient(endpoint);
                ClientHandle<CLIENT> existingHandle = clientRegistry.registerClient(client, endpoint);
                if (existingHandle != null) {
                    destroyClient(existingHandle.client());
                }
                return client;
            }

            // client needs to be singleton only for target server/cluster
            ClientSingletonOnly<Signature> singletonOnly = ClientSingletonOnly.class.cast(this);
            Signature signature = singletonOnly.getSignature(endpoint.getOptions());
            SignatureGroup signatureGroup = clientRegistry.matchSignatureGroup(this.getClass(), signature);
            // no client for target server/cluster yet
            if (signatureGroup == null) {
                CLIENT client = createClient(endpoint);
                ClientHandle<CLIENT> existingHandle = clientRegistry.registerClient(client, endpoint);

                if (existingHandle != null) {
                    clientRegistry.removeSignatureGroup(this.getClass(), endpoint.getId());
                    destroyClient(existingHandle.client());
                }

                signatureGroup = new SignatureGroup(endpoint.getId());
                signatureGroup.addSignature(signature);
                clientRegistry.addSignatureGroup(this.getClass(), signatureGroup);

                return client;
            } else {
                if (signatureGroup.id().equals(endpoint.getId())) {
                    return clientRegistry.getClient(endpoint.getId());
                } else {
                    LOGGER.error(
                            "Another client instance with id {} already connects to target server/cluster that allows only 1 client instance",
                            signatureGroup.id()
                    );
                    return null;
                }
            }
        }
    }

    public final void destroy(String id, Class<CLIENT> clientType) {
        synchronized (lock) {
            CLIENT client = clientRegistry.unregisterClient(id, clientType);
            if (client != null) {
                destroyClient(client);
            }

            if (ClientSingletonOnly.class.isInstance(this)) {
                clientRegistry.removeSignatureGroup(this.getClass(), id);
            }
        }
    }

    /**
     * Create a client with given options
     * @param endpoint
     *          - {@link Endpoint} needed by this {@link ClientFactory} to create and get hold of client instance
     * @return
     *          a client instance
     */
    protected abstract CLIENT createClient(Endpoint endpoint);
    protected abstract void destroyClient(CLIENT client);
}
