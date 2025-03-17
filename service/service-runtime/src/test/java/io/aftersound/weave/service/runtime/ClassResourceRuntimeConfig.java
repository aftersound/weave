package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.actor.ActorBindingsConfig;
import io.aftersound.component.ComponentConfig;
import io.aftersound.component.ComponentRegistry;
import io.aftersound.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ClassResourceRuntimeConfig implements RuntimeConfig {

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final String NAMESPACE = "WEAVE";
    private static final String APPLICATION = "service";
    private static final String RUNTIME_CONFIG = String.format("/%s/%s/runtime-config.json", NAMESPACE, APPLICATION);

    private final JsonNode runtimeConfig;

    public ClassResourceRuntimeConfig() {
        try (InputStream is = ClassResourceRuntimeConfig.class.getResourceAsStream(RUNTIME_CONFIG)) {
            runtimeConfig = MAPPER.readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred on read " + RUNTIME_CONFIG, e);
        }
    }

    @Override
    public ServiceInstance getServiceInstance() {
        return new ServiceInstance() {

            @Override
            public String getId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getNamespace() {
                return NAMESPACE;
            }

            @Override
            public String getApplication() {
                return APPLICATION;
            }

            @Override
            public String getEnvironment() {
                return "test";
            }

            @Override
            public String getHost() {
                return "localhost";
            }

            @Override
            public int getPort() {
                return 8080;
            }

            @Override
            public String getIpv4Address() {
                return "127.0.0.1";
            }

            @Override
            public String getIpv6Address() {
                return null;
            }
        };
    }

    @Override
    public ComponentRegistry getBootstrapComponentRegistry() {
        return null;
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.Json;
    }

    @Override
    public ConfigUpdateStrategy getConfigUpdateStrategy() {
        return ConfigUpdateStrategy.ondemand();
    }

    @Override
    public ConfigProvider getConfigProvider() {
        return new ConfigProvider() {
            @Override
            protected ConfigHolder getConfig() {
                return new ConfigHolder(runtimeConfig) {
                    @Override
                    protected List<ActorBindingsConfig> extractExtensionConfigs() {
                        try {
                            ActorBindingsConfig[] extensionConfigs = MAPPER.treeToValue(
                                    this.config().get("extensions"),
                                    ActorBindingsConfig[].class
                            );
                            return Arrays.asList(extensionConfigs);
                        } catch (Exception e) {
                            throw new RuntimeException("Exception occurred on extract 'extensions'", e);
                        }
                    }

                    @Override
                    protected List<ComponentConfig> extractComponentConfigs(ObjectMapper configReader) {
                        try {
                            ComponentConfig[] componentConfigs = MAPPER.treeToValue(
                                    this.config().get("components"),
                                    ComponentConfig[].class
                            );
                            return Arrays.asList(componentConfigs);
                        } catch (Exception e) {
                            throw new RuntimeException("Exception occurred on extract 'components'", e);
                        }
                    }

                    @Override
                    protected List<ServiceMetadata> extractServiceMetadata(ObjectMapper configReader) {
                        try {
                            ServiceMetadata[] serviceMetadataArray = MAPPER.treeToValue(
                                    this.config().get("services"),
                                    ServiceMetadata[].class
                            );
                            return Arrays.asList(serviceMetadataArray);
                        } catch (Exception e) {
                            throw new RuntimeException("Exception occurred on extract 'services'", e);
                        }
                    }
                };
            }
        };
    }

}
