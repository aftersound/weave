package io.aftersound.weave.job.runner;

import io.aftersound.weave.utils.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Agent {

    private static final Logger LOGGER = LoggerFactory.getLogger(Agent.class);

    private final Instance instance;

    private final int totalSlots;
    private final AtomicInteger availableSlots;
    private final ExecutorService jobExecutor;

    private final Client client;
    private final WebTarget webTarget;
    private final ScheduledExecutorService heartbeatScheduler;
    private final long heartbeatInterval;

    public Agent(Instance instance, int totalSlots, Client client, String jobManagerUri, long heartbeatInterval) {
        this.instance = instance;
        this.totalSlots = totalSlots;
        this.availableSlots = new AtomicInteger(totalSlots);
        this.jobExecutor = Executors.newFixedThreadPool(totalSlots);
        this.client = client;
        this.webTarget = client.target(jobManagerUri);
        this.heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        this.heartbeatInterval = heartbeatInterval;
    }

    public void start() {
        if (!registerRunner()) {
            throw new RuntimeException("Job Runner failed to register");
        }

        // start heartbeat thread
        heartbeatScheduler.scheduleAtFixedRate(
                new HeartbeatRunnable(),
                50L,
                heartbeatInterval,
                TimeUnit.MILLISECONDS
        );
    }

    public void stop() {
        // stop the thread which reports heath state to management plane
        heartbeatScheduler.shutdownNow();

        unregisterRunner();
        client.close();
    }

    public Map<String, String> getCapability() {
        return instance.getCapability();
    }

    public Map<String, Object> getCapacity() {
        Runtime rt = Runtime.getRuntime();
        return MapBuilder.linkedHashMap()
                .kv("totalSlots", totalSlots)
                .kv("availableSlots", availableSlots.get())
                .kv("availableProcessors", rt.availableProcessors())
                .kv("totalMemory", rt.totalMemory())
                .kv("freeMemory", rt.freeMemory())
                .kv("maxMemory", rt.maxMemory())
                .build();
    }

    private boolean registerRunner() {
        // PUT /job/runner/register
        try {
            WebTarget target = webTarget.path("/job/runner/register");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(instance, MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /job/runner/register success");
                return true;
            } else {
                String msg = String.format(
                        "PUT /job/runner/register error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("PUT /job/runner/register exception:", e);
            return false;
        }
    }

    private boolean unregisterRunner() {
        // PUT /job/runner/unregister
        try {
            WebTarget target = webTarget.path("/job/runner/unregister");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(instance, MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /job/runner/unregister success");
                return false;
            } else {
                String msg = String.format(
                        "PUT /job/runner/unregister error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("PUT /job/runner/unregister exception: ", e);
            return false;
        }
    }

    private void sendHeartbeat() {
        // PUT /job/runner/heartbeat
        try {
            WebTarget target = webTarget.path("/job/runner/heartbeat");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(getHeartbeat(), MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /job/runner/heartbeat success");
            } else {
                String msg = String.format(
                        "PUT /job/runner/heartbeat error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
            }
        } catch (Exception e) {
            LOGGER.error("PUT /job/runner/heartbeat exception", e);
        }
    }

    private Heartbeat getHeartbeat() {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setInstance(instance);

        Capacity capacity = new Capacity();
        capacity.setTotalSlots(totalSlots);
        capacity.setAvailableSlots(availableSlots.get());
        heartbeat.setCapacity(capacity);

        heartbeat.setMetrics(getMetrics());
        return heartbeat;
    }

    private Map<String, Object> getMetrics() {
        Runtime rt = Runtime.getRuntime();
        return MapBuilder.linkedHashMap()
                .kv("availableProcessors", rt.availableProcessors())
                .kv("totalMemory", rt.totalMemory())
                .kv("freeMemory", rt.freeMemory())
                .kv("maxMemory", rt.maxMemory())
                .build();
    }

    private String parseErrorMessage(Response response) {
        try {
            Map<String, Object> error = response.readEntity(Map.class);
            List<Map<String, Object>> messages = (List<Map<String, Object>>) error.get("messages");
            return (String) messages.get(0).get("message");
        } catch (Exception e) {
            return "unknown error";
        }
    }

    private class HeartbeatRunnable implements Runnable {

        @Override
        public void run() {
            sendHeartbeat();
        }

    }

}
