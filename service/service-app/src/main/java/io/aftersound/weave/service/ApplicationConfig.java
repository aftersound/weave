package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.rl.RateLimitControlRegistry;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.runtime.*;
import io.aftersound.weave.service.security.AuthControlRegistry;
import io.aftersound.weave.service.security.AuthHandler;
import io.aftersound.weave.utils.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Base64;

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
    protected void setup() throws Exception {
        LOGGER.info("(1) Identify service instance...");
        ServiceInstance serviceInstance = identifyServiceInstance();
        LOGGER.info("service instance information: \n{}", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInstance));

        LOGGER.info("(2) Obtain service runtime bootstrap config...");
        ServiceRuntimeBootstrapConfig bootstrapConfig;
        try {
            String content = properties.getRuntimeBootstrapConfig();
            if (content.startsWith("BASE64|")) {
                byte[] decoded = Base64.getDecoder().decode(content.substring("BASE64|".length()));
                content = new String(decoded, "UTF-8");
            }
            content = StringHandle.of(content).value();
            bootstrapConfig = MAPPER.readValue(content, ServiceRuntimeBootstrapConfig.class);
            LOGGER.info("...service runtime bootstrap config obtained");
        } catch (Exception e) {
            LOGGER.error("...failed to obtain service runtime bootstrap config", e);
            throw e;
        }

        LOGGER.info("(3) Create/initialize service runtime bootstrap components...");
        ComponentRegistry componentRegistry;
        try {
            componentRegistry = createAndInitBootstrapComponents(bootstrapConfig);
            LOGGER.info("...service runtime bootstrap config created/initialized");
        } catch (Exception e) {
            LOGGER.error("...failed to create/initialize service runtime bootstrap components", e);
            throw e;
        }

        LOGGER.info("(4) Create/obtain config for service runtime...");
        ClientAndNamespaceAwareRuntimeConfig runtimeConfig;
        try {
            Class<?> clazz = Class.forName(bootstrapConfig.getRuntimeConfigClass());
            if (!ClientAndNamespaceAwareRuntimeConfig.class.isAssignableFrom(clazz)) {
                throw new Exception("'runtime.config.class' specified in application.properties is not supported");
            }
            Class<? extends ClientAndNamespaceAwareRuntimeConfig> runtimeConfigClass =
                    (Class<? extends ClientAndNamespaceAwareRuntimeConfig>)clazz;

            runtimeConfig = runtimeConfigClass
                    .getDeclaredConstructor(
                            ComponentRegistry.class,
                            String.class,
                            String.class,
                            ConfigFormat.class,
                            ConfigUpdateStrategy.class)
                    .newInstance(
                            componentRegistry,
                            "runtimeConfigSource",
                            serviceInstance.getNamespace(),
                            bootstrapConfig.configFormat(),
                            bootstrapConfig.configUpdateStrategy()
                    );
            runtimeConfig.setServiceInstance(serviceInstance);
            LOGGER.info("...service runtime config created and obtained.");
        } catch (Exception e) {
            LOGGER.error("...failed to create/obtain service runtime config", e);
            throw e;
        }

        LOGGER.info("(5) Bootstrap service runtime with obtained config...");
        RuntimeComponents components;
        try {
            components = new RuntimeWeaver().bindAndWeave(runtimeConfig);
            components.initializer().init(false);
            this.components = components;
            LOGGER.info("...service runtime is bootstrapped successfully.");
        } catch (Exception e) {
            LOGGER.error("...failed to bootstrap service runtime", e);
            throw e;
        }

        if (runtimeConfig.getConfigUpdateStrategy().isAutoRefresh()) {
            LOGGER.info("Service runtime config update strategy is {}", "AutoRefresh");
            runtimeConfig.getConfigUpdateStrategy().openAutoRefreshStartGate();
            LOGGER.info("Service runtime config auto refresh daemon started");
        } else {
            LOGGER.info("Service runtime config update strategy is {}", "OnDemand");
        }

    }

    @PreDestroy
    protected void teardown() {
        LOGGER.info("Shutting down service runtime...");
        LOGGER.info("Service runtime is shut down successfully.");
    }

    private ServiceInstance identifyServiceInstance() throws Exception {
        InetAddress ia = InetAddress.getLocalHost();
        String hostName = ia.getHostName();
        String hostAddress = ia.getHostAddress();

        ServiceInstanceInfo info = new ServiceInstanceInfo();
        info.setNamespace(properties.getNamespace());
        info.setEnvironment(properties.getEnvironment());
        info.setHostName(hostName);
        info.setHostAddress(hostAddress);

        return info;
    }

    protected static ComponentRegistry createAndInitBootstrapComponents(ServiceRuntimeBootstrapConfig bootstrapConfig) throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings =
                ActorBindingsUtil.loadActorBindings(
                        bootstrapConfig.getComponentFactoryTypes(),
                        ComponentConfig.class,
                        Object.class,
                        false
                );

        ObjectMapper objectMapper = ObjectMapperBuilder.forJson()
                .with(
                        new BaseTypeDeserializer<>(
                                ComponentConfig.class,
                                "type",
                                componentFactoryBindings.controlTypes().all()
                        )
                )
                .build();

        ComponentConfig[] componentConfigList = objectMapper.readValue(
                objectMapper.writeValueAsBytes(bootstrapConfig.getComponentConfigs()),
                ComponentConfig[].class
        );

        ComponentRegistry componentRegistry = new ComponentRegistry(componentFactoryBindings);
        for (ComponentConfig endpoint : componentConfigList) {
            componentRegistry.initializeComponent(endpoint);
        }

        return componentRegistry;
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
    protected AuthControlRegistry authControlRegistry() {
        return components.authControlRegistry();
    }

    @Bean
    protected ActorRegistry<AuthHandler> authHandlerRegistry() {
        return components.authHandlerRegistry();
    }

    @Bean
    protected RateLimitControlRegistry rateLimitControlRegistry() {
        return components.rateLimitControlRegistry();
    }

    @Bean
    protected ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry() {
        return components.rateLimitEvaluatorRegistry();
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
