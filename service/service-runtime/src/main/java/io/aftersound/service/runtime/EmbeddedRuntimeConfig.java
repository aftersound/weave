package io.aftersound.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.actor.ActorBindingsConfig;
import io.aftersound.component.ComponentConfig;
import io.aftersound.jackson.BaseTypeDeserializer;
import io.aftersound.jackson.ObjectMapperBuilder;
import io.aftersound.service.cache.CacheControl;
import io.aftersound.service.cache.KeyControl;
import io.aftersound.service.metadata.ExecutionControl;
import io.aftersound.service.metadata.ServiceMetadata;
import io.aftersound.service.rl.RateLimitControl;
import io.aftersound.service.security.AuthControl;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

class EmbeddedRuntimeConfig {

    private static final List<ActorBindingsConfig> extensionConfigList;
    private static final List<ComponentConfig> componentConfigList;
    private static final List<ServiceMetadata> serviceMetadataList;

    static {
        try (InputStream is = EmbeddedRuntimeConfig.class.getResourceAsStream("/runtime-config.json")) {
            JsonNode runtimeConfig = new ObjectMapper().readTree(is);

            ActorBindingsConfig[] extensionConfigs = new ObjectMapper().treeToValue(
                    runtimeConfig.get("extensions"),
                    ActorBindingsConfig[].class
            );
            ActorBindingsSet abs = ExtensionHelper.loadAndInitActorBindings(Arrays.asList(extensionConfigs));

            ObjectMapper componentConfigReader = ObjectMapperBuilder.forJson()
                    .with(
                            new BaseTypeDeserializer<>(
                                    ComponentConfig.class,
                                    "type",
                                    abs.componentFactoryBindings.controlTypes().all()
                            )
                    )
                    .build();

            ComponentConfig[] componentConfigs = componentConfigReader.treeToValue(
                    runtimeConfig.get("components"),
                    ComponentConfig[].class
            );

            BaseTypeDeserializer<ExecutionControl> executionControlTypeDeserializer =
                    new BaseTypeDeserializer<>(
                            ExecutionControl.class,
                            "type",
                            abs.serviceExecutorBindings.controlTypes().all()
                    );

            BaseTypeDeserializer<CacheControl> cacheControlBaseTypeDeserializer =
                    new BaseTypeDeserializer<>(
                            CacheControl.class,
                            "type",
                            abs.cacheFactoryBindings.controlTypes().all()
                    );

            BaseTypeDeserializer<KeyControl> keyControlBaseTypeDeserializer =
                    new BaseTypeDeserializer<>(
                            KeyControl.class,
                            "type",
                            abs.cacheKeyGeneratorBindings.controlTypes().all()
                    );

            BaseTypeDeserializer<AuthControl> authControlBaseTypeDeserializer =
                    new BaseTypeDeserializer<>(
                            AuthControl.class,
                            "type",
                            abs.authHandlerBindings.controlTypes().all()
                    );

            BaseTypeDeserializer<RateLimitControl> rateLimitControlBaseTypeDeserializer =
                    new BaseTypeDeserializer<>(
                            RateLimitControl.class,
                            "type",
                            abs.rateLimitEvaluatorBindings.controlTypes().all()
                    );

            ObjectMapper serviceMetadataReader = ObjectMapperBuilder.forJson()
                    .with(executionControlTypeDeserializer)
                    .with(cacheControlBaseTypeDeserializer)
                    .with(keyControlBaseTypeDeserializer)
                    .with(authControlBaseTypeDeserializer)
                    .with(rateLimitControlBaseTypeDeserializer)
                    .build();
            ServiceMetadata[] serviceMetadatas = serviceMetadataReader.treeToValue(
                    runtimeConfig.get("services"),
                    ServiceMetadata[].class
            );

            extensionConfigList = Arrays.asList(extensionConfigs);
            componentConfigList = Arrays.asList(componentConfigs);
            serviceMetadataList = Arrays.asList(serviceMetadatas);
        } catch (Exception e) {
            throw new RuntimeException("failed to read embedded runtime config", e);
        }
    }

    public static List<ActorBindingsConfig> getExtensionConfigList() {
        return extensionConfigList;
    }

    public static List<ComponentConfig> getComponentConfigList() {
        return componentConfigList;
    }

    public static List<ServiceMetadata> getServiceMetadataList() {
        return serviceMetadataList;
    }

}
