package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.runtime.*;
import io.aftersound.weave.service.security.Authenticator;
import io.aftersound.weave.service.security.Authorizer;
import io.aftersound.weave.service.security.SecurityControlRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Configuration
@EnableMBeanExport
public class ApplicationConfig {

    private static final ObjectMapper JSON_MAPPER = ObjectMapperBuilder.forJson().build();

    private final ApplicationProperties properties;

    private RuntimeComponents components;

    public ApplicationConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    protected void initialize() throws Exception {
        RuntimeConfig runtimeConfig = createAndInitRuntimeConfig(properties);
        this.components = new RuntimeWeaver().initAndWeave(runtimeConfig);
        runtimeConfig.getConfigUpdateStrategy().openAutoRefreshStartGate();
    }

    public static RuntimeConfig createAndInitRuntimeConfig(ApplicationProperties properties) throws Exception {
        String namespace = properties.getNamespace();
        ConfigFormat configFormat = getConfigFormat(properties);
        ConfigUpdateStrategy configUpdateStrategy = getConfigUpdateStrategy(properties);
        Endpoint endpoint = JSON_MAPPER.readValue(properties.getBootstrapClientConfig(), Endpoint.class);

        ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings = ActorBindingsUtil.loadActorBindings(
                Collections.singletonList(properties.getBootstrapClientFactory()),
                Endpoint.class,
                Object.class,
                false
        );
        ClientRegistry clientRegistry = new ClientRegistry(clientFactoryBindings);

        clientRegistry.initializeClient(endpoint);

        Class<?> clazz = Class.forName(properties.getRuntimeConfigClass());
        if (!ClientAndNamespaceAwareRuntimeConfig.class.isAssignableFrom(clazz)) {
            throw new Exception("'runtime.config' specified in application.properties is not supported");
        }
        Class<? extends ClientAndNamespaceAwareRuntimeConfig> runtimeConfigClass =
                (Class<? extends ClientAndNamespaceAwareRuntimeConfig>)clazz;
        ClientAndNamespaceAwareRuntimeConfig runtimeConfig = runtimeConfigClass
                .getDeclaredConstructor(
                        ClientRegistry.class,
                        String.class,
                        String.class,
                        ConfigFormat.class,
                        ConfigUpdateStrategy.class)
                .newInstance(
                        clientRegistry,
                        endpoint.getId(),
                        namespace,
                        configFormat,
                        configUpdateStrategy
                );
        return runtimeConfig;
    }

    private static ConfigUpdateStrategy getConfigUpdateStrategy(ApplicationProperties properties) {
        if ("AutoRefresh".equalsIgnoreCase(properties.getConfigUpdateStrategy())) {
            long autoRefreshInterval = 5000L;
            try {
                autoRefreshInterval = Long.parseLong(properties.getConfigAutoRefreshInternal());
            } catch (Exception e) {
            }
            return ConfigUpdateStrategy.autoRefresh(autoRefreshInterval);
        } else {
            return ConfigUpdateStrategy.ondemand();
        }
    }

    private static ConfigFormat getConfigFormat(ApplicationProperties properties) {
        if (ConfigFormat.Yaml.name().equalsIgnoreCase(properties.getConfigFormat())) {
            return ConfigFormat.Yaml;
        } else {
            return ConfigFormat.Json;
        }
    }

    @Bean
    protected ParameterProcessor<HttpServletRequest> parameterProcessor() {
        return components.parameterProcessor();
    }

    @Bean
    protected CacheRegistry cacheRegistry() {
        return components.cacheRegistry();
    }

    @Bean
    protected ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry() {
        return components.cacheKeyGeneratorRegistry();
    }

    @Bean
    protected SecurityControlRegistry securityControlRegistry() {
        return components.securityControlRegistry();
    }

    @Bean
    protected ActorRegistry<Authenticator> authenticatorRegistry() {
        return components.authenticatorRegistry();
    }

    @Bean
    protected ActorRegistry<Authorizer> authorizerRegistry() {
        return components.authorizerRegistry();
    }

    @Bean
    @Qualifier("adminServiceMetadataRegistry")
    protected ServiceMetadataRegistry adminServiceMetadataRegistry() {
        return components.adminServiceMetadataRegistry();
    }

    @Bean
    @Qualifier("adminServiceExecutorFactory")
    protected ServiceExecutorFactory adminServiceExecutorFactory() {
        return components.adminServiceExecutorFactory();
    }

    @Bean
    @Qualifier("serviceMetadataRegistry")
    protected ServiceMetadataRegistry serviceMetadataRegistry() {
        return components.serviceMetadataRegistry();
    }

    @Bean
    @Qualifier("serviceExecutorFactory")
    protected ServiceExecutorFactory serviceExecutorFactory() {
        return components.serviceExecutorFactory();
    }

}