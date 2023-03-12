package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

class EmbeddedRuntimeConfig {

    private static JsonNode getRuntimeConfig() {
        try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/runtime-config.json")) {
            return new ObjectMapper().readTree(is);
        } catch (IOException e) {
            throw new RuntimeException("failed to read embedded runtime config", e);
        }
    }

    public static ConfigProvider<ActorBindingsConfig> getExtensionConfigProvider() {
        return new ConfigProvider<ActorBindingsConfig>() {
            @Override
            protected List<ActorBindingsConfig> getConfigList() {
                JsonNode runtimeConfigJsonNode = getRuntimeConfig();
                try {
                    ActorBindingsConfig[] extensionConfigs = configReader.treeToValue(
                            runtimeConfigJsonNode.get("extensions"),
                            ActorBindingsConfig[].class
                    );
                    return Arrays.asList(extensionConfigs);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on parse 'extensions'", e);
                }
            }
        };
    }

    public static ConfigProvider<ComponentConfig> getComponentConfigProvider() {
        return new ConfigProvider<ComponentConfig>() {
            @Override
            protected List<ComponentConfig> getConfigList() {
                JsonNode runtimeConfigJsonNode = getRuntimeConfig();
                try {
                    ComponentConfig[] componentConfigs = configReader.treeToValue(
                            runtimeConfigJsonNode.get("components"),
                            ComponentConfig[].class
                    );
                    return Arrays.asList(componentConfigs);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on parse 'components'", e);
                }
            }
        };
    }

    public static ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                JsonNode runtimeConfigJsonNode = getRuntimeConfig();
                try {
                    ServiceMetadata[] serviceMetadatas = configReader.treeToValue(
                            runtimeConfigJsonNode.get("services"),
                            ServiceMetadata[].class
                    );
                    return Arrays.asList(serviceMetadatas);
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on parse 'services'", e);
                }
            }
        };
    }

}
