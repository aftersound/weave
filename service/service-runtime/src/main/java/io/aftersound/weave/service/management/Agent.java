package io.aftersound.weave.service.management;

import io.aftersound.weave.config.Config;
import io.aftersound.weave.jersey.ClientHandle;
import io.aftersound.weave.jersey.ClientHandleCreator;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.utils.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.aftersound.weave.service.management.AgentConfigDictionary.*;

public class Agent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    private final boolean enabled;
    private final ServiceInstance serviceInstance;
    private final ClientHandle clientHandle;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long heartbeatInterval;

    public Agent(Config config, ServiceInstance serviceInstance) {
        boolean enabled = config.v(ENABLED);

        ClientHandle clientHandle = null;
        ScheduledExecutorService scheduledExecutorService = null;
        if (enabled) {
            clientHandle = ClientHandleCreator.create(
                    MapBuilder.hashMap()
                            .kv("uri", config.vRequired(MANAGER))
                            .build()
            );

            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        this.enabled = enabled;
        this.serviceInstance = serviceInstance;
        this.clientHandle = clientHandle;
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
            clientHandle.close();
        }
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
