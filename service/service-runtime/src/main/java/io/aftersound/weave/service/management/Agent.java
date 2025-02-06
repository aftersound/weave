package io.aftersound.weave.service.management;

import io.aftersound.config.Config;
import io.aftersound.util.MapBuilder;
import io.aftersound.util.Pair;
import io.aftersound.util.StringHandle;
import io.aftersound.weave.service.ServiceInstance;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
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

    public Agent(ServiceInstance serviceInstance) {
        Config config = getConfig();

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

            if (!registerInstance()) {
                throw new RuntimeException("Service instance failed to register");
            }

            // start heartbeat thread
            scheduledExecutorService.scheduleAtFixedRate(
                    new HeartbeatRunnable(),
                    50L,
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

    private static Config getConfig() {
        Map<String, String> cfg = new HashMap<>();
        cfg.put(ENABLED.name(), StringHandle.of("${WEAVE_SERVICE_AGENT_ENABLED}").value());
        cfg.put(MANAGER.name(), StringHandle.of("${WEAVE_SERVICE_MANAGER}").value());

        String str = StringHandle.of("${WEAVE_SERVICE_HEARTBEAT_INTERVAL}").value();
        try {
            Long.parseLong(str);
            cfg.put(HEARTBEAT_INTERVAL.name(), str);
        } catch (Exception e) {
            cfg.put(HEARTBEAT_INTERVAL.name(), "30000");
        }

        return Config.from(cfg, KEYS);
    }

    private Pair<Client, WebTarget> createClient(Config config) {
        final String uri = config.v(MANAGER);

        ClientConfig clientConfig = new ClientConfig();
//        if (authType != null) {
//            clientConfig = clientConfig.property(AUTH_TYPE.name(), authType);
//        }

        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target = client.target(uri);
        return Pair.of(client, target);
    }

    private boolean registerInstance() {
        // PUT /service/instance/register
        try {
            WebTarget target = webTarget.path("/service/instance/register");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(getServiceInstance(), MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /service/instance/register success");
                return true;
            } else {
                String msg = String.format(
                        "PUT /service/instance/register error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("PUT /service/instance/register exception:", e);
            return false;
        }
    }

    private boolean unregisterInstance() {
        // PUT /service/instance/unregister
        try {
            WebTarget target = webTarget.path("/service/instance/unregister");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(getServiceInstance(), MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /service/instance/unregister success");
                return false;
            } else {
                String msg = String.format(
                        "PUT /service/instance/unregister error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("PUT /service/instance/unregister exception: ", e);
            return false;
        }
    }

    private void sendHeartbeat() {
        // PUT /service/instance/heartbeat
        try {
            WebTarget target = webTarget.path("/service/instance/heartbeat");

            Response response = target
                    .request(MediaType.APPLICATION_JSON)
                    .buildPut(Entity.entity(getHeartbeat(), MediaType.APPLICATION_JSON_TYPE))
                    .invoke();

            if (response.getStatus() == 200) {
                LOGGER.info("PUT /service/instance/heartbeat success");
            } else {
                String msg = String.format(
                        "PUT /service/instance/heartbeat error: status=%d, message=%s",
                        response.getStatus(),
                        parseErrorMessage(response)
                );
                LOGGER.error(msg);
            }
        } catch (Exception e) {
            LOGGER.error("PUT /service/instance/heartbeat exception", e);
        }
    }

    private Map<String, Object> getServiceInstance() {
        return MapBuilder.<String, Object>linkedHashMap()
                .put("id", serviceInstance.getId())
                .put("namespace", serviceInstance.getNamespace())
                .put("application", serviceInstance.getApplication())
                .put("environment", serviceInstance.getEnvironment())
                .put("host", serviceInstance.getHost())
                .put("port", serviceInstance.getPort())
                .put("ipv4Address", serviceInstance.getIpv4Address())
                .put("ipv6Address", serviceInstance.getIpv6Address())
                .build();
    }

    private Map<String, Object> getHeartbeat() {
        return MapBuilder.<String, Object>linkedHashMap()
                .put("instance", getServiceInstance())
                .put("metrics", getMetrics())
                .build();
    }

    private Map<String, Object> getMetrics() {
        Runtime rt = Runtime.getRuntime();
        return MapBuilder.<String, Object>linkedHashMap()
                .put("availableProcessors", rt.availableProcessors())
                .put("totalMemory", rt.totalMemory())
                .put("freeMemory", rt.freeMemory())
                .put("maxMemory", rt.maxMemory())
                .build();
    }

    @SuppressWarnings("unchecked")
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
