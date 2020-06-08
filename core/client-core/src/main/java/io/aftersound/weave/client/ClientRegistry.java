package io.aftersound.weave.client;

import io.aftersound.weave.actor.ActorBindings;

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
        dcfr.getClientFactory(endpoint.getType()).create(endpoint);
    }

    public void destroyClient(String type, String id) throws Exception {
        Class<Object> clientType = dcfr.getClientType(type);
        dcfr.getClientFactory(type).destroy(id, clientType);
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
            clientInfo.setClientType(clientHandle.client().getClass().getName());
        }
        return clientInfoList;
    }

    public ClientInfo getClientInfo(String id) {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setId(id);

        ClientHandle<?> clientHandle = clientHandleById.get(id);
        if (clientHandle != null) {
            clientInfo.setClientType(clientHandle.client().getClass().getName());
        }

        return clientInfo;
    }
}
