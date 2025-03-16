package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.actor.ActorBindingsConfig;
import io.aftersound.component.ComponentConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ConfigHolder {

    protected static final ObjectMapper MAPPER = new ObjectMapper();

    private final JsonNode config;

    public ConfigHolder(JsonNode config) {
        this.config = config;
    }

    public JsonNode config() {
        return config;
    }

    protected final List<ActorBindingsConfig> getExtensionConfigList() {
        Map<String, ActorBindingsConfig> abcByGroup = new LinkedHashMap<>();

        List<ActorBindingsConfig> cl = this.extractExtensionConfigs();
        cl.forEach(
                c -> {
                    ActorBindingsConfig abc = abcByGroup.get(c.getGroup());
                    if (abc == null) {
                        abc = new ActorBindingsConfig();
                        abc.setGroup(c.getGroup());
                        abc.setBaseType(c.getBaseType());
                        abc.setTypes(new ArrayList<>());
                        abcByGroup.put(c.getGroup(), abc);
                    }
                    abc.getTypes().addAll(c.getTypes());
                }
        );

        EmbeddedRuntimeConfig.getExtensionConfigList().forEach(
                c -> {
                    ActorBindingsConfig abc = abcByGroup.get(c.getGroup());
                    if (abc == null) {
                        abc = new ActorBindingsConfig();
                        abc.setGroup(c.getGroup());
                        abc.setBaseType(c.getBaseType());
                        abc.setTypes(new ArrayList<>());
                        abcByGroup.put(c.getGroup(), abc);
                    }
                    abc.getTypes().addAll(c.getTypes());
                }
        );
        return new ArrayList<>(abcByGroup.values());
    }

    protected abstract List<ActorBindingsConfig> extractExtensionConfigs();

    protected final List<ComponentConfig> getComponentConfigList(ObjectMapper configReader) {
        List<ComponentConfig> componentConfigList = new ArrayList<>(extractComponentConfigs(configReader));
        componentConfigList.addAll(EmbeddedRuntimeConfig.getComponentConfigList());
        return componentConfigList;
    }

    protected abstract List<ComponentConfig> extractComponentConfigs(ObjectMapper configReader);

    protected final List<ServiceMetadata> getServiceMetadataList(ObjectMapper serviceMetadataReader) {
        List<ServiceMetadata> serviceMetadataList = new ArrayList<>(extractServiceMetadata(serviceMetadataReader));
        serviceMetadataList.addAll(EmbeddedRuntimeConfig.getServiceMetadataList());
        return serviceMetadataList;
    }

    protected abstract List<ServiceMetadata> extractServiceMetadata(ObjectMapper configReader);

}
