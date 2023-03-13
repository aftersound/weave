package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.request.CoreParameterProcessor;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.rl.RateLimitControl;
import io.aftersound.weave.service.rl.RateLimitControlRegistry;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.security.AuthControl;
import io.aftersound.weave.service.security.AuthControlRegistry;
import io.aftersound.weave.service.security.AuthHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RuntimeWeaver {

    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    /**
     * Instantiate extensions, bind and weave them into service runtime core based on runtime configuration
     *
     * @param runtimeConfig
     *          service runtime configuration
     * @return {@link RuntimeComponents}
     *          which provides access points of runtime
     * @throws Exception
     *          any exception during binding and weave
     */
    public RuntimeComponents bindAndWeave(RuntimeConfig runtimeConfig) throws Exception {

        // 1.{ load and init ActorBindings of service extension points
        ExtensionConfigProvider extensionConfigProvider = runtimeConfig.getExtensionConfigProvider();
        List<ActorBindingsConfig> actorBindingsConfigList = extensionConfigProvider.configs();
        ActorBindingsSet abs = ExtensionHelper.loadAndInitActorBindings(actorBindingsConfigList);
        // } load and init ActorBindings of service extension points


        // 2.{ create and stitch to form component management runtime core
        ComponentConfigProvider componentConfigProvider = runtimeConfig.getComponentConfigProvider();
        ObjectMapper componentConfigReader = createComponentConfigReader(
                runtimeConfig.getConfigFormat(),
                abs.componentFactoryBindings.controlTypes()
        );
        componentConfigProvider.setConfigReader(componentConfigReader);
        ComponentRegistry componentRegistry = new ComponentRegistry(abs.componentFactoryBindings);
        ComponentManager componentManager = new ComponentManager(
                componentConfigProvider,
                runtimeConfig.getConfigUpdateStrategy(),
                componentRegistry
        );
        // } create and stitch to form component management runtime core


        // 3.{ create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry = new ActorFactory<>(abs.cacheKeyGeneratorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ObjectMapper serviceMetadataReader = createServiceMetadataReader(
                runtimeConfig.getConfigFormat(),
                abs.serviceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.authHandlerBindings.controlTypes(),
                abs.rateLimitEvaluatorBindings.controlTypes()
        );

        ServiceMetadataProvider serviceMetadataProvider = runtimeConfig.getServiceMetadataProvider();
        serviceMetadataProvider.setConfigReader(serviceMetadataReader);
        ServiceMetadataManager serviceMetadataManager = new ServiceMetadataManager(
                "service.metadata",
                serviceMetadataProvider,
                runtimeConfig.getConfigUpdateStrategy(),
                cacheRegistry
        );

        ManagedComponentRepository managedComponentRepository = new ManagedComponentRepository(
                runtimeConfig.getServiceInstance(),
                new ComponentRepositoryImpl(runtimeConfig.getBootstrapComponentRegistry()),
                new ComponentRepositoryImpl(componentRegistry),
                cacheRegistry,
                componentManager,
                serviceMetadataManager
        );

        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(
                managedComponentRepository,
                abs.serviceExecutorBindings.actorTypes()
        );

        ParameterProcessor<HttpServletRequest> parameterProcessor = new CoreParameterProcessor(managedComponentRepository);
        // } create and stitch to form service execution runtime core

        // 4.{ authentication and authorization related
        AuthControlRegistry authControlRegistry = new AuthControlRegistry(serviceMetadataManager);

        ActorRegistry<AuthHandler> authHandlerRegistry = new ActorFactory<>(abs.authHandlerBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);
        for (NamedType<AuthControl> authControlNamedType : abs.authHandlerBindings.controlTypes().all()) {
            AuthHandler<?> authHandler = authHandlerRegistry.get(authControlNamedType.name());
            authHandler.setComponentRegistry(componentRegistry);
        }
        // } authentication and authorization related

        // 5.{ rate limit related
        RateLimitControlRegistry rateLimitControlRegistry = new RateLimitControlRegistry(serviceMetadataManager);

        ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry = new ActorFactory<>(abs.rateLimitEvaluatorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);
        for (NamedType<RateLimitControl> namedType : abs.rateLimitEvaluatorBindings.controlTypes().all()) {
            RateLimitEvaluator<?> rateLimitEvaluator = rateLimitEvaluatorRegistry.get(namedType.name());
            rateLimitEvaluator.setComponentRegistry(componentRegistry);
        }
        // } authentication and authorization related

        // 7.expose those needed for request serving
        RuntimeComponentsImpl components = new RuntimeComponentsImpl();

        components.setServiceMetadataRegistry(serviceMetadataManager);
        components.setServiceExecutorFactory(serviceExecutorFactory);
        components.setParameterProcessor(parameterProcessor);
        components.setCacheRegistry(cacheRegistry);
        components.setCacheKeyGeneratorRegistry(cacheKeyGeneratorRegistry);

        components.setAuthControlRegistry(authControlRegistry);
        components.setAuthHandlerRegistry(authHandlerRegistry);

        components.setRateLimitControlRegistry(rateLimitControlRegistry);
        components.setRateLimitEvaluatorRegistry(rateLimitEvaluatorRegistry);

        components.setInitializer(
                new InitializerComposite(
                        Arrays.asList(
                                componentManager,
                                serviceExecutorFactory,
                                serviceMetadataManager
                        )
                )
        );

        Manageable<ServiceInstance> serviceInstanceManageable = new Manageable<ServiceInstance>() {

            @Override
            public ManagementFacade<ServiceInstance> getManagementFacade() {

                return new ManagementFacade<ServiceInstance>() {
                    @Override
                    public String name() {
                        return "service.instance.info";
                    }

                    @Override
                    public Class<ServiceInstance> entityType() {
                        return ServiceInstance.class;
                    }

                    @Override
                    public void refresh() {
                    }

                    @Override
                    public List<ServiceInstance> list() {
                        return Collections.singletonList(runtimeConfig.getServiceInstance());
                    }

                    @Override
                    public ServiceInstance get(String id) {
                        return runtimeConfig.getServiceInstance();
                    }
                };
            }

        };

        components.setManagementFacades(new ManagementFacadesImpl(
                serviceInstanceManageable,
                componentManager,
                serviceMetadataManager,
                serviceExecutorFactory
        ));

        return components;
    }

    private ObjectMapperBuilder configReaderBuilder(ConfigFormat configFormat) {
        if (configFormat == ConfigFormat.Yaml) {
            return ObjectMapperBuilder.forYAML();
        } else {
            return ObjectMapperBuilder.forJson();
        }
    }

    private ObjectMapper createComponentConfigReader(ConfigFormat configFormat, NamedTypes<ComponentConfig> controlTypes) {
        return configReaderBuilder(configFormat)
                .with(
                        new BaseTypeDeserializer<>(
                                ComponentConfig.class,
                                "type",
                                controlTypes.all()
                        )
                )
                .build();
    }

    private ObjectMapper createServiceMetadataReader(
            ConfigFormat configFormat,
            NamedTypes<ExecutionControl> executionControlTypes,
            NamedTypes<CacheControl> cacheControlTypes,
            NamedTypes<KeyControl> keyControlTypes,
            NamedTypes<AuthControl> authControlTypes,
            NamedTypes<RateLimitControl> rateLimitControlTypes) {

        BaseTypeDeserializer<ExecutionControl> executionControlTypeDeserializer =
                new BaseTypeDeserializer<>(
                        ExecutionControl.class,
                        "type",
                        executionControlTypes.all()
                );

        BaseTypeDeserializer<CacheControl> cacheControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        CacheControl.class,
                        "type",
                        cacheControlTypes.all()
                );

        BaseTypeDeserializer<KeyControl> keyControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        KeyControl.class,
                        "type",
                        keyControlTypes.all()
                );

        BaseTypeDeserializer<AuthControl> authControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthControl.class,
                        "type",
                        authControlTypes.all()
                );

        BaseTypeDeserializer<RateLimitControl> rateLimitControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        RateLimitControl.class,
                        "type",
                        rateLimitControlTypes.all()
                );

        return configReaderBuilder(configFormat)
                .with(executionControlTypeDeserializer)
                .with(cacheControlBaseTypeDeserializer)
                .with(keyControlBaseTypeDeserializer)
                .with(authControlBaseTypeDeserializer)
                .with(rateLimitControlBaseTypeDeserializer)
                .build();
    }

}
