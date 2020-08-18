package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ClientRegistry} keeps track of each data client and its identifier and
 * also provides access to data client. Within same registry, each identifier are
 * expected to be unique.
 *
 * Note:
 * This is not thread safe since it's not expected to see data clients
 * get registered/unregistered by different entities in different threads.
 */
public final class ClientRegistry {

    private final ClientFactoryRegistry dcfr;
    private final Map<String, ClientHandle<?>> clientHandleById = new HashMap<>();
    private final Map<Class<? extends ClientFactory>, List<SignatureGroup>> signatureGroupsByFactory = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientRegistry.class);

    public ClientRegistry(ActorBindings<Endpoint, ClientFactory<?>, Object> dataClientFactoryBindings) {
        try {
            this.dcfr = new ClientFactoryRegistry(this, dataClientFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <CLIENT> ClientHandle<CLIENT> registerClient(CLIENT client, Endpoint endpoint) {
        if ( client == null || endpoint == null || endpoint.getId() == null || endpoint.getOptions() == null) {
            return null;
        }

        return (ClientHandle<CLIENT>) clientHandleById.put(
                endpoint.getId(),
                ClientHandle.of(client, endpoint));
    }

    <CLIENT> ClientHandle<CLIENT> getClientHandle(String id) {
        return (ClientHandle<CLIENT>)clientHandleById.get(id);
    }

    <CLIENT> CLIENT unregisterClient(String id, Class<CLIENT> type) {
        ClientHandle existingClientHandle = clientHandleById.remove(id);
        if (existingClientHandle != null) {
            return type.cast(existingClientHandle.client());
        } else {
            return null;
        }
    }

    SignatureGroup matchSignatureGroup(Class<? extends ClientFactory> dcfClass, Signature signature) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(dcfClass);
        if (signatureGroups == null || signatureGroups.isEmpty()) {
            return null;
        }

        for (SignatureGroup group : signatureGroups) {
            if (group.hasMatching(signature)) {
                return group;
            }
        }
        return null;
    }

    void addSignatureGroup(Class<? extends ClientFactory> dcfClass, SignatureGroup signatureGroup) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(dcfClass);
        if (signatureGroups == null) {
            signatureGroups = new ArrayList<>();
            signatureGroupsByFactory.put(dcfClass, signatureGroups);
        }
        signatureGroups.add(signatureGroup);
    }

    void removeSignatureGroup(Class<? extends ClientFactory> dcfClass, String id) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(dcfClass);
        if (signatureGroups != null && !signatureGroups.isEmpty()) {
            int target = -1;
            for (int index = 0; index < signatureGroups.size(); index++) {
                if (signatureGroups.get(index).id().equals(id)) {
                    target = index;
                    break;
                }
            }
            if (target >= 0) {
                signatureGroups.remove(target);
            }
        }
    }

    public void initializeClient(Endpoint endpoint) throws Exception {
        LOGGER.info("");
        LOGGER.info("Creating client with type '{}' and id '{}'", endpoint.getType(), endpoint.getId());
        ClientFactory<?> clientFactory = dcfr.getClientFactory(endpoint.getType());
        if (clientFactory != null) {
            LOGGER.info("...using ClientFactory {}", clientFactory);
        } else {
            LOGGER.error("...no ClientFactory associated with type '{}' in registry", endpoint.getType());
        }
        Object client = clientFactory.create(endpoint);
        LOGGER.info("as {}", client);
    }

    public void destroyClient(String type, String id) throws Exception {
        LOGGER.info("");
        LOGGER.info("Destroying client with type '{}' and id '{}'", type, id);
        ClientFactory<Object> clientFactory = dcfr.getClientFactory(type);
        if (clientFactory != null) {
            LOGGER.info("...using ClientFactory {}", clientFactory);
        } else {
            LOGGER.error("...no ClientFactory associated with type '{}' in registry", type);
        }
        Class<Object> clientType = dcfr.getClientType(type);
        LOGGER.info("...client type {}", clientType);
        clientFactory.destroy(id, clientType);
        LOGGER.info("destroyed");
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> CLIENT getClient(String id) {
        ClientHandle clientHandle = clientHandleById.get(id);
        if (clientHandle != null) {
            return (CLIENT)clientHandle.client();
        }
        return null;
    }

    public List<ClientInfo> getClientInfos() {
        List<ClientInfo> clientInfoList = new ArrayList<>();
        for (ClientHandle<?> clientHandle : clientHandleById.values()) {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setId(clientHandle.endpoint().getId());
            clientInfo.setControlType(clientHandle.endpoint().getType());
            clientInfo.setClientType(clientHandle.client().getClass().getName());
            clientInfoList.add(clientInfo);
        }
        return clientInfoList;
    }

    public ClientInfo getClientInfo(String id) {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId(id);

        ClientHandle<?> clientHandle = clientHandleById.get(id);
        if (clientHandle != null) {
            clientInfo.setControlType(clientHandle.endpoint().getType());
            clientInfo.setClientType(clientHandle.client().getClass().getName());
        }

        return clientInfo;
    }
}
