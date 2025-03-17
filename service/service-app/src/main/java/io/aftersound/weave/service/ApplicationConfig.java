package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.util.ContentHandle;
import io.aftersound.util.StringHandle;
import io.aftersound.actor.ActorBindings;
import io.aftersound.actor.ActorBindingsUtil;
import io.aftersound.actor.ActorRegistry;
import io.aftersound.component.ComponentConfig;
import io.aftersound.component.ComponentFactory;
import io.aftersound.component.ComponentRegistry;
import io.aftersound.jackson.BaseTypeDeserializer;
import io.aftersound.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.management.Agent;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.rl.RateLimitControlRegistry;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.runtime.*;
import io.aftersound.weave.service.security.AuthControlRegistry;
import io.aftersound.weave.service.security.AuthHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.event.EventListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableMBeanExport
public class ApplicationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private final ApplicationProperties properties;
    private final ServiceInstanceInfo serviceInstance;

    private RuntimeComponents components;
    private Agent agent;

    public ApplicationConfig(ApplicationProperties properties) {
        this.properties = properties;
        this.serviceInstance = new ServiceInstanceInfo();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @PostConstruct
    protected void setup() throws Exception {
        LOGGER.info("(1) Identify service instance...");
        identifyServiceInstance();
        LOGGER.info("service instance information: \n{}", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(serviceInstance));

        LOGGER.info("(2) Obtain service runtime bootstrap config...");
        ServiceRuntimeBootstrapConfig bootstrapConfig;
        try {
            String content = ContentHandle.of(properties.getBootstrapConfig()).getString();
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
        ClientAndApplicationAwareRuntimeConfig runtimeConfig;
        try {
            Class<?> clazz = Class.forName(bootstrapConfig.getRuntimeConfigClass());
            if (!ClientAndApplicationAwareRuntimeConfig.class.isAssignableFrom(clazz)) {
                throw new Exception("'runtime.config.class' specified in application.properties is not supported");
            }
            Class<? extends ClientAndApplicationAwareRuntimeConfig> runtimeConfigClass =
                    (Class<? extends ClientAndApplicationAwareRuntimeConfig>)clazz;

            runtimeConfig = runtimeConfigClass
                    .getDeclaredConstructor(
                            ComponentRegistry.class,
                            String.class,
                            String.class,
                            String.class,
                            ConfigFormat.class,
                            ConfigUpdateStrategy.class)
                    .newInstance(
                            componentRegistry,
                            "runtimeConfigSource",
                            serviceInstance.getNamespace(),
                            serviceInstance.getApplication(),
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

        LOGGER.info("(6) Bootstrap service management agent...");
        this.agent = new Agent(serviceInstance);
    }

    @EventListener
    protected void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        serviceInstance.setPort(event.getWebServer().getPort());
        LOGGER.info("service instance is ready at port : {}", serviceInstance.getPort());

        agent.start();
    }

    @PreDestroy
    protected void teardown() {
        LOGGER.info("Shutting down service runtime...");
        LOGGER.info("Service runtime is shut down successfully.");

        agent.stop();
    }

    private void identifyServiceInstance() throws Exception {
        InetAddress ia = InetAddress.getLocalHost();
        // TODO: instance id could be pre-assigned via system property or environment variable?
        serviceInstance.setId(UUID.randomUUID().toString());
        serviceInstance.setNamespace(properties.getNamespace());
        serviceInstance.setApplication(properties.getApplication());
        serviceInstance.setEnvironment(properties.getEnvironment());
        serviceInstance.setHost(ia.getHostName());
        serviceInstance.setIpv4Address(ia.getHostAddress());
        serviceInstance.setIpv6Address(null);  // TODO
        ServiceInstanceHolder.set(serviceInstance);
    }

    protected static ComponentRegistry createAndInitBootstrapComponents(ServiceRuntimeBootstrapConfig bootstrapConfig) throws Exception {
        List<String> componentTypes = bootstrapConfig.getComponentFactoryTypes();
        if (componentTypes == null) {
            componentTypes = Collections.emptyList();
        }
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings =
                ActorBindingsUtil.loadActorBindings(
                        componentTypes,
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

        List<Map<String, Object>> componentConfigs = bootstrapConfig.getComponentConfigs();
        if (componentConfigs == null) {
            componentConfigs = Collections.emptyList();
        }
        ComponentConfig[] componentConfigList = objectMapper.readValue(
                objectMapper.writeValueAsBytes(componentConfigs),
                ComponentConfig[].class
        );

        ComponentRegistry componentRegistry = new ComponentRegistry(componentFactoryBindings);
        for (ComponentConfig componentConfig : componentConfigList) {
            componentRegistry.initializeComponent(componentConfig);
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
