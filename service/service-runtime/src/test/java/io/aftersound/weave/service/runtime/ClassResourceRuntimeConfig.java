package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.io.InputStream;
import java.util.List;

public class ClassResourceRuntimeConfig implements RuntimeConfig {

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final String NAMESPACE = "WEAVE";
    private static final String APPLICATION = "service";
    private static final String ACTOR_BINDINGS_CONFIG_LIST = String.format("/%s/%s/actor.bindings.config.list.json", NAMESPACE, APPLICATION);
    private static final String COMPONENT_CONFIG_LIST = String.format("/%s/%s/component.config.list.json", NAMESPACE, APPLICATION);
    private static final String SERVICE_METADATA_LIST = String.format("/%s/%s/service.metadata.list.json", NAMESPACE, APPLICATION);
    private static final String ADMIN_SERVICE_METADATA_LIST = String.format("/%s/%s/admin.service.metadata.list.json", NAMESPACE, APPLICATION);

    @Override
    public ServiceInstance getServiceInstance() {
        return new ServiceInstance() {

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
            public String getHostName() {
                return "localhost";
            }

            @Override
            public String getHostAddress() {
                return "127.0.0.1";
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
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ConfigProvider<ActorBindingsConfig>() {
            @Override
            protected List<ActorBindingsConfig> getConfigList() {
                try (InputStream is = ClassResourceRuntimeConfig.class.getResourceAsStream(ACTOR_BINDINGS_CONFIG_LIST)) {
                    return MAPPER.readValue(is, new TypeReference<List<ActorBindingsConfig>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on read " + ACTOR_BINDINGS_CONFIG_LIST, e);
                }
            }
        };
    }

    @Override
    public ConfigProvider<ComponentConfig> getComponentConfigProvider() {
        return new ConfigProvider<ComponentConfig>() {
            @Override
            protected List<ComponentConfig> getConfigList() {
                try (InputStream is = ClassResourceRuntimeConfig.class.getResourceAsStream(COMPONENT_CONFIG_LIST)) {
                    return MAPPER.readValue(is, new TypeReference<List<ComponentConfig>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on read " + COMPONENT_CONFIG_LIST, e);
                }
            }
        };
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                try (InputStream is = ClassResourceRuntimeConfig.class.getResourceAsStream(SERVICE_METADATA_LIST)) {
                    return MAPPER.readValue(is, new TypeReference<List<ServiceMetadata>>() {});
                } catch (Exception e) {
                    throw new RuntimeException("Exception occurred on read " + SERVICE_METADATA_LIST, e);
                }
            }
        };
    }

}
