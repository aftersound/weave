package io.aftersound.service.management;

import java.util.Map;

public class Heartbeat {

    private Instance instance;
    private Map<String, Object> metrics;

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public Map<String, Object> getMetrics() {
        return metrics;
    }

    public void setMetrics(Map<String, Object> metrics) {
        this.metrics = metrics;
    }

}
