package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This {@link DataClientManager} manages the lifecycle of {@link Endpoint} (s) and
 * interacts with {@link DataClientRegistry} to manage the lifecycle of corresponding
 * data clients.
 */
class DataClientManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataClientManager.class);

    private final ExecutorService dataClientConfigPollThread = Executors.newSingleThreadExecutor();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final ObjectMapper dataClientConfigReader;
    private final Path metadataDirectory;
    private final DataClientRegistry dataClientRegistry;

    protected volatile Map<String, Endpoint> endpointById = Collections.emptyMap();

    public DataClientManager(
            ObjectMapper dataClientConfigReader,
            Path metadataDirectory,
            DataClientRegistry dataClientRegistry) {
        this.dataClientConfigReader = dataClientConfigReader;
        this.metadataDirectory = metadataDirectory;
        this.dataClientRegistry = dataClientRegistry;
    }

    public void init() {
        if (!shutdown.get()) {
            loadAndInit();

            // kick off data client config polling thread
            dataClientConfigPollThread.submit(this::refreshMetadataInLoop);
        }
    }

    /**
     * Shut down and clean up
     */
    public void shutdown() {
        // stop data client config polling thread
        shutdown.compareAndSet(false, true);
    }

    /**
     * Periodically, fixed at every 5 seconds, load {@link Endpoint} (s) from metadata directory
     */
    private void refreshMetadataInLoop() {
        try {
            while (!shutdown.get()) {
                try {
                    loadAndInit();
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    LOGGER.warn("{}: data client config poll thread is interrupted", this, e); // not expected
                    break;
                }
            }
            LOGGER.info("{}: Returning from data client config pooling", this);
        } catch (Exception e) { // mostly an unrecoverable.
            LOGGER.error("{}: Exception while reading data client config from directory", this, e);
            throw e;
        }
    }

    private void loadAndInit() {
        // load data client config
        Map<String, Endpoint> endpointById = new DataClientConfigLoader(
                dataClientConfigReader,
                metadataDirectory
        ).load();

        // identify removed
        Map<String, Endpoint> removed = figureOutRemoved(this.endpointById, endpointById);

        this.endpointById = endpointById;

        // initialize data client for each loaded Endpoint
        for (Endpoint endpoint : endpointById.values()) {
            try {
                dataClientRegistry.initializeClient(endpoint.getType(), endpoint.getId(), endpoint.getOptions());
            } catch (Exception e) {
                LOGGER.error("{} occurred while initializing data client of type {} with id {}", e, endpoint.getType(), endpoint.getId());
            }
        }

        // destroy data clients to be removed
        for (Endpoint endpoint : removed.values()) {
            try {
                dataClientRegistry.destroyClient(endpoint.getType(), endpoint.getId());
            } catch (Exception e) {
                LOGGER.error("{} occurred while destroying data client of type {} with id {}", e, endpoint.getType(), endpoint.getId());
            }
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
