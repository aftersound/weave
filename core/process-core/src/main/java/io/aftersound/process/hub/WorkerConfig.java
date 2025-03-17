package io.aftersound.process.hub;

import java.util.List;
import java.util.Map;

public class WorkerConfig {

    private String workerId;
    private List<Map<String, String>> pipelineConfigs;

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public List<Map<String, String>> getPipelineConfigs() {
        return pipelineConfigs;
    }

    public void setPipelineConfigs(List<Map<String, String>> pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs;
    }

}
