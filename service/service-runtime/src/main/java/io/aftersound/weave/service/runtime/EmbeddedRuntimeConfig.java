package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.core.type.TypeReference;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

class EmbeddedRuntimeConfig {

    public static ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ConfigProvider<ActorBindingsConfig>() {
            @Override
            protected List<ActorBindingsConfig> getConfigList() {
                try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/actor.bindings.config.list.json")) {
                    return configReader.readValue(is, new TypeReference<List<ActorBindingsConfig>>() {});
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }
        };
    }

    public static ConfigProvider<ComponentConfig> getComponentConfigProvider() {
        return new ConfigProvider<ComponentConfig>() {
            @Override
            protected List<ComponentConfig> getConfigList() {
                try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/component.config.list.json")) {
                    return configReader.readValue(is, new TypeReference<List<ComponentConfig>>() {});
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }
        };
    }

    public static ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/service.metadata.list.json")) {
                    return configReader.readValue(is, new TypeReference<List<ServiceMetadata>>() {});
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }
        };
    }

    public static ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/admin.service.metadata.list.json")) {
                    return configReader.readValue(is, new TypeReference<List<ServiceMetadata>>() {});
                } catch (IOException e) {
                    return Collections.emptyList();
                }
            }
        };
    }

}
