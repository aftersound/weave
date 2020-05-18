package io.aftersound.weave.service.runtime;

import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This {@link ClientManager} manages the lifecycle of {@link Endpoint} (s) and
 * interacts with {@link ClientRegistry} to manage the lifecycle of corresponding
 * data clients.
 */
final class ClientManager extends WithConfigAutoRefreshMechanism implements Manageable<Endpoint> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

    private static final boolean TOLERATE_EXCEPTION = true;

    private final String name;
    private final ConfigProvider<Endpoint> clientConfigProvider;
    private final ClientRegistry clientRegistry;

    protected volatile Map<String, Endpoint> endpointById = Collections.emptyMap();

    public ClientManager(
            String name,
            ConfigProvider<Endpoint> clientConfigProvider,
            ConfigUpdateStrategy configUpdateStrategy,
            ClientRegistry clientRegistry) {
        super(configUpdateStrategy);

        this.name = name;
        this.clientConfigProvider = clientConfigProvider;
        this.clientRegistry = clientRegistry;
    }

    @Override
    synchronized void loadConfigs(boolean tolerateException) {
        // load client configs from provider
        List<Endpoint> endpoints = Collections.emptyList();
        try {
            endpoints = clientConfigProvider.getConfigList();
        } catch (Exception e) {
            LOGGER.error("Exception occurred when loading client configs from provider", e);
            if (tolerateException) {
                return;
            } else {
                throwException(e);
            }
        }

        Map<String, Endpoint> endpointById = endpoints.stream().collect(Collectors.toMap(Endpoint::getId, endpoint -> endpoint ));

        // identify removed
        Map<String, Endpoint> removed = figureOutRemoved(this.endpointById, endpointById);

        this.endpointById = endpointById;

        // initialize client for each loaded Endpoint
        for (Endpoint endpoint : endpointById.values()) {
            try {
                clientRegistry.initializeClient(endpoint);
            } catch (Exception e) {
                LOGGER.error("Exception occurred when initializing client of type {} with id {}", endpoint.getType(), endpoint.getId(), e);
                if (!tolerateException) {
                    throwException(e);
                }
            }
        }

        // destroy clients to be removed
        for (Endpoint endpoint : removed.values()) {
            try {
                clientRegistry.destroyClient(endpoint.getType(), endpoint.getId());
            } catch (Exception e) {
                LOGGER.error("Exception occurred when destroying client of type {} with id {}", endpoint.getType(), endpoint.getId(), e);
            }
        }
    }

    @Override
    public ManagementFacade<Endpoint> getManagementFacade() {

        return new ManagementFacade<Endpoint>() {

            @Override
            public String name() {
                return name;
            }

            @Override
            public Class<Endpoint> entityType() {
                return Endpoint.class;
            }

            @Override
            public void refresh() {
                loadConfigs(TOLERATE_EXCEPTION);
            }

            @Override
            public List<Endpoint> list() {
                return new ArrayList<>(endpointById.values());
            }

            @Override
            public Endpoint get(String id) {
                return endpointById.get(id);
            }

        };

    }

    private void throwException(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
        } else {
            throw new RuntimeException(e);
        }
    }

    private Map<String, Endpoint> figureOutRemoved(Map<String, Endpoint> existing, Map<String, Endpoint> latest) {
        Set<String> retained = new HashSet<>(existing.keySet());
        retained.retainAll(latest.keySet());

        Set<String> removed = new HashSet<>(existing.keySet());
        removed.removeAll(retained);

        Map<String, Endpoint> result = new HashMap<>(removed.size());
        for (String id : removed) {
            result.put(id, existing.get(id));
        }
        return result;
    }

}
