package io.aftersound.weave.service.management;

import io.aftersound.weave.config.Config;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.utils.MapBuilder;
import io.aftersound.weave.utils.Pair;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.aftersound.weave.service.management.AgentConfigDictionary.*;

public class Agent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    private final boolean enabled;
    private final ServiceInstance serviceInstance;
    private final Client client;
    private final WebTarget webTarget;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long heartbeatInterval;

    public Agent(Config config, ServiceInstance serviceInstance) {
        boolean enabled = config.v(ENABLED);

        Pair<Client, WebTarget> clientAndWebTarget = Pair.of(null, null);
        ScheduledExecutorService scheduledExecutorService = null;
        if (enabled) {
            clientAndWebTarget = createClient(config);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        this.enabled = enabled;
        this.serviceInstance = serviceInstance;
        this.client = clientAndWebTarget.first();
        this.webTarget = clientAndWebTarget.second();
        this.scheduledExecutorService = scheduledExecutorService;
        this.heartbeatInterval = config.v(HEARTBEAT_INTERVAL);
    }

    public void start() {
        if (enabled) {
            registerInstance();

            // start heartbeat thread
            scheduledExecutorService.scheduleAtFixedRate(
                    new HeartbeatRunnable(),
                    500L,
                    heartbeatInterval,
                    TimeUnit.MILLISECONDS
            );
        }
    }

    public void stop() {
        if (enabled) {
            // stop the thread which reports heath state to management plane
            scheduledExecutorService.shutdownNow();

            unregisterInstance();
            client.close();
        }
    }

    private Pair<Client, WebTarget> createClient(Config config) {
        final String uri = config.v(MANAGER);

        ClientConfig clientConfig = new ClientConfig();
//        if (authType != null) {
//            clientConfig = clientConfig.property(AUTH_TYPE.name(), authType);
//        }

        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target(uri);
//        if (path != null) {
//            target = target.path(path);
//        }
        return Pair.of(client, target);
    }

    private void registerInstance() {
    }

    private void unregisterInstance() {
    }

    private Map<String, Object> getPerformanceMetrics() {
        Runtime rt = Runtime.getRuntime();
        return MapBuilder.linkedHashMap()
                .kv("availableProcessors", rt.availableProcessors())
                .kv("totalMemory", rt.totalMemory())
                .kv("freeMemory", rt.freeMemory())
                .kv("maxMemory", rt.maxMemory())
                .build();
    }

    private class HeartbeatRunnable implements Runnable {

        @Override
        public void run() {
            LOGGER.info("send heartbeat...");
        }

    }

}
