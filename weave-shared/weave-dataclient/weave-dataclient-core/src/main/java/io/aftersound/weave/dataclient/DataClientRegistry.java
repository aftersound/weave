package io.aftersound.weave.dataclient;

import io.aftersound.weave.actor.ActorBindings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link DataClientRegistry} keeps track of each data client and its identifier and
 * also provides access to data client. Within same registry, each identifier are
 * expected to be unique.
 *
 * Note:
 * This is not thread safe since it's not expected to see data clients
 * get registered/unregistered by different entities in different threads.
 */
public final class DataClientRegistry {

    private final DataClientFactoryRegistry dcfr;
    private final Map<String, ClientHandle<?>> clientHandleById = new HashMap<>();
    private final Map<Class<? extends DataClientFactory>, List<SignatureGroup>> signatureGroupsByFactory = new HashMap<>();

    public DataClientRegistry(ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings) {
        try {
            this.dcfr = new DataClientFactoryRegistry(this, dataClientFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    <CLIENT> ClientHandle<CLIENT> registerClient(String id, Map<String, Object> options, CLIENT client) {
        if (id == null || options == null || client == null) {
            return null;
        }

        return (ClientHandle<CLIENT>) clientHandleById.put(id, ClientHandle.of(client).bindOptionsHash(options.hashCode()));
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

    SignatureGroup matchSignatureGroup(Class<? extends DataClientFactory> dcfClass, Signature signature) {
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

    void addSignatureGroup(Class<? extends DataClientFactory> dcfClass, SignatureGroup signatureGroup) {
        List<SignatureGroup> signatureGroups = signatureGroupsByFactory.get(dcfClass);
        if (signatureGroups == null) {
            signatureGroups = new ArrayList<>();
            signatureGroupsByFactory.put(dcfClass, signatureGroups);
        }
        signatureGroups.add(signatureGroup);
    }

    void removeSignatureGroup(Class<? extends DataClientFactory> dcfClass, String id) {
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

    public void initializeClient(String type, String id, Map<String, Object> options) throws Exception {
        dcfr.getDataClientFactory(type).create(id, options);
    }

    public void destroyClient(String type, String id) throws Exception {
        Class<Object> clientType = dcfr.getDataClientType(type);
        dcfr.getDataClientFactory(type).destroy(id, clientType);
    }

    @SuppressWarnings("unchecked")
    public <CLIENT> CLIENT getClient(String id) {
        ClientHandle clientHandle = clientHandleById.get(id);
        if (clientHandle != null) {
            return (CLIENT)clientHandle.client();
        }
        return null;
    }

}
