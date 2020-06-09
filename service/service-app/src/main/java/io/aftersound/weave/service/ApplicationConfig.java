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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Collections;

@Configuration
@EnableMBeanExport
public class ApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private final ApplicationProperties properties;

    private RuntimeComponents components;

    public ApplicationConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    protected void initialize() throws Exception {
        LOGGER.info("Identify information of this service instance...");
        ServiceInstance serviceInstance = identifyServiceInstance();
        LOGGER.info("Service instance information: {}", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInstance));
        LOGGER.info("Information of this service instance is identified");

        LOGGER.info("Creating and obtaining bootstrap config for service runtime...");
        ClientAndNamespaceAwareRuntimeConfig runtimeConfig;
        try {
            runtimeConfig = createAndInitRuntimeConfig(properties);
        } catch (Exception e) {
            LOGGER.error("Exception occurred on creating/obtaining bootstrap config based on application properties", e);
            throw e;
        }
        runtimeConfig.setServiceInstance(serviceInstance);
        LOGGER.info("Service runtime bootstrap config created and obtained.");

        LOGGER.info("Bootstrapping service runtime with bootstrap config...");
        RuntimeComponents components;
        try {
            components = new RuntimeWeaver().bindAndWeave(runtimeConfig);
        } catch (Exception e) {
            LOGGER.error("Exception occurred on bootstrapping service runtime with bootstrap config", e);
            throw e;
        }
        components.initializer().init(false);

        this.components = components;

        LOGGER.info("Service runtime is bootstrapped successfully.");

        if (runtimeConfig.getConfigUpdateStrategy().isAutoRefresh()) {
            LOGGER.info("Service runtime config update strategy is {}", "AutoRefresh");
            runtimeConfig.getConfigUpdateStrategy().openAutoRefreshStartGate();
            LOGGER.info("Service runtime config auto refresh daemon started");
        } else {
            LOGGER.info("Service runtime config update strategy is {}", "OnDemand");
        }

    }

    private ServiceInstance identifyServiceInstance() throws Exception {
        InetAddress ia = InetAddress.getLocalHost();
        String hostName = ia.getHostName();
        String hostAddress = ia.getHostAddress();

        ServiceInstanceInfo info = new ServiceInstanceInfo();
        info.setApplicationName(properties.getApplicationName());
        info.setNamespace(properties.getNamespace());
        info.setEnvironment(properties.getEnvironment());
        info.setHostName(hostName);
        info.setHostAddress(hostAddress);

        return info;
    }

    public static ClientAndNamespaceAwareRuntimeConfig createAndInitRuntimeConfig(ApplicationProperties properties) throws Exception {
        String namespace = properties.getNamespace();
        ConfigFormat configFormat = getConfigFormat(properties);
        ConfigUpdateStrategy configUpdateStrategy = getConfigUpdateStrategy(properties);
        Endpoint endpoint = MAPPER.readValue(properties.getBootstrapClientConfig(), Endpoint.class);

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
    protected ServiceManagementFacadesMBean serviceManagementFacadesMBean() {
        return new ServiceManagementFacades(components.managementFacades());
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
