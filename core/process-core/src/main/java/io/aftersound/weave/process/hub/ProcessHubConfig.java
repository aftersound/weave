package io.aftersound.weave.process.hub;

import java.util.List;
import java.util.Map;

public class ProcessHubConfig {

    private List<String> workers;
    private Map<String, Map<String, String>> pipelineConfigs;
    private List<WorkerConfig> workerConfigs;

    public List<String> getWorkers() {
        return workers;
    }

    public void setWorkers(List<String> workers) {
        this.workers = workers;
    }

    public Map<String, Map<String, String>> getPipelineConfigs() {
        return pipelineConfigs;
    }

    public void setPipelineConfigs(Map<String, Map<String, String>> pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs;
    }

    public List<WorkerConfig> getWorkerConfigs() {
        return workerConfigs;
    }

    public void setWorkerConfigs(List<WorkerConfig> workerConfigs) {
        this.workerConfigs = workerConfigs;
    }

}
