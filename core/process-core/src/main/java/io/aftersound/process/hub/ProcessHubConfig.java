package io.aftersound.process.hub;

import java.util.*;

public class ProcessHubConfig {

    private List<Map<String, Object>> pipelineBlueprints;
    private List<Map<String, Object>> pipelineConfigs;

    public List<Map<String, Object>> getPipelineBlueprints() {
        return pipelineBlueprints;
    }

    public void setPipelineBlueprints(List<Map<String, Object>> pipelineBlueprints) {
        this.pipelineBlueprints = pipelineBlueprints;
    }

    public List<Map<String, Object>> getPipelineConfigs() {
        return pipelineConfigs;
    }

    public void setPipelineConfigs(List<Map<String, Object>> pipelineConfigs) {
        this.pipelineConfigs = pipelineConfigs;
    }

    public Map<String, Map<String, Object>> pipelineBaseConfigs() {
        Map<String, Map<String, Object>> baseConfigById = new HashMap<>();
        if (pipelineBlueprints != null) {
            for (Map<String, Object> baseConfig : pipelineBlueprints) {
                String id = (String) baseConfig.get("id");
                baseConfigById.put(id, baseConfig);
            }
        }
        return Collections.unmodifiableMap(baseConfigById);
    }

    public List<Map<String, Object>> pipelineConfigs() {
        Map<String, Map<String, Object>> pipelineBaseConfigs = pipelineBaseConfigs();

        List<Map<String, Object>> pipelineCompleteConfigs = new ArrayList<>(pipelineConfigs.size());
        for (Map<String, Object> pipelineConfig : pipelineConfigs) {
            Map<String, Object> pipelineCompleteConfig = new LinkedHashMap<>(pipelineConfig);

            // include base config if exists
            String base = (String) pipelineCompleteConfig.remove("base");
            if (base != null && pipelineBaseConfigs.containsKey(base)) {
                Map<String, Object> baseConfig = new LinkedHashMap<>(pipelineBaseConfigs.get(base));
                baseConfig.remove("id");
                pipelineCompleteConfig.putAll(baseConfig);
            }

            pipelineCompleteConfigs.add(Collections.unmodifiableMap(pipelineCompleteConfig));
        }
        return Collections.unmodifiableList(pipelineCompleteConfigs);
    }

}
