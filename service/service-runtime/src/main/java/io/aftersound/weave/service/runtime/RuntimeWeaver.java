package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.common.*;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.ManagedComponents;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.Validation;
import io.aftersound.weave.service.request.*;
import io.aftersound.weave.service.security.Auth;
import io.aftersound.weave.service.security.AuthControl;
import io.aftersound.weave.service.security.AuthControlRegistry;
import io.aftersound.weave.service.security.AuthHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeWeaver {

    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    /**
     * Instantiate extensions, bind and weave them into service runtime core based on runtime configuration
     * @param runtimeConfig
     *          service runtime configuration
     * @return
     *          {@link RuntimeComponents} which provides access points of runtime
     * @throws Exception
     *          any exception during binding and wevae
     */
    public RuntimeComponents bindAndWeave(RuntimeConfig runtimeConfig) throws Exception {

        // 1.{ load and init ActorBindings of service extension points
        ConfigProvider<ActorBindingsConfig> actorBindingsConfigProvider = runtimeConfig.getActorBindingsConfigProvider();
        actorBindingsConfigProvider.setConfigReader(configReaderBuilder(runtimeConfig.getConfigFormat()).build());
        List<ActorBindingsConfig> actorBindingsConfigList = actorBindingsConfigProvider.getConfigList();
        ActorBindingsSet abs = loadAndInitActorBindings(actorBindingsConfigList);
        // } load and init ActorBindings of service extension points


        // 2.{ create and stitch to form component management runtime core
        ConfigProvider<ComponentConfig> componentConfigProvider = runtimeConfig.getComponentConfigProvider();
        componentConfigProvider.setConfigReader(configReaderBuilder(runtimeConfig.getConfigFormat()).build());
        ComponentRegistry componentRegistry = new ComponentRegistry(abs.componentFactoryBindings);
        ComponentManager componentManager = new ComponentManager(
                componentConfigProvider,
                runtimeConfig.getConfigUpdateStrategy(),
                componentRegistry
        );
        // } create and stitch to form client management runtime core


        // 3.{ create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry = new ActorFactory<>(abs.cacheKeyGeneratorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<Validator> paramValidatorRegistry = new ActorFactory<>(abs.validatorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<Deriver> paramDeriverRegistry = new ActorFactory<>(abs.deriverBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<CodecFactory> codecFactoryRegistry = new ActorFactory<>(abs.codecFactoryBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry = new ActorFactory<>(abs.valueFuncFactoryBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ObjectMapper serviceMetadataReader = createServiceMetadataReader(
                runtimeConfig.getConfigFormat(),
                abs.serviceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.validatorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authHandlerBindings.controlTypes()
        );

        ConfigProvider<ServiceMetadata> serviceMetadataProvider = runtimeConfig.getServiceMetadataProvider();
        serviceMetadataProvider.setConfigReader(serviceMetadataReader);
        ServiceMetadataManager serviceMetadataManager = new ServiceMetadataManager(
                "service.metadata",
                serviceMetadataProvider,
                runtimeConfig.getConfigUpdateStrategy(),
                cacheRegistry
        );

        ManagedComponents managedResources = new ManagedComponentsImpl();

        // make dataFormatRegistry available to non-admin/normal services
        managedResources.setComponent(Constants.CODEC_FACTORY_REGISTRY, codecFactoryRegistry);

        // make componentRegistry available to non-admin/normal services
        managedResources.setComponent(ComponentRegistry.class.getName(), componentRegistry);

        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(
                managedResources,
                abs.serviceExecutorBindings.actorTypes()
        );

        ParameterProcessor<HttpServletRequest> parameterProcessor = new CoreParameterProcessor(
                valueFuncFactoryRegistry,
                codecFactoryRegistry,
                paramValidatorRegistry,
                paramDeriverRegistry
        );

        // } create and stitch to form service execution runtime core


        // 4.{ stitch administration service runtime core
        ObjectMapper adminServiceMetadataReader = createServiceMetadataReader(
                runtimeConfig.getConfigFormat(),
                abs.adminServiceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.validatorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authHandlerBindings.controlTypes()
        );
        ConfigProvider<ServiceMetadata> adminServiceMetadataProvider = runtimeConfig.getAdminServiceMetadataProvider();
        adminServiceMetadataProvider.setConfigReader(adminServiceMetadataReader);
        ServiceMetadataManager adminServiceMetadataManager = new ServiceMetadataManager(
                "protected.service.metadata",
                adminServiceMetadataProvider,
                ConfigUpdateStrategy.ondemand(),
                null
        );
        adminServiceMetadataManager.loadConfigs(DO_NOT_TOLERATE_EXCEPTION);

        // make following beans available to administration services
        // for admin purpose only
        ManagedComponents adminOnlyResources = new ManagedComponentsImpl();

        adminOnlyResources.setComponent(ServiceInstance.class.getName(), runtimeConfig.getServiceInstance());
        adminOnlyResources.setComponent(Constants.ADMIN_COMPONENT_REGISTRY, runtimeConfig.getBootstrapComponentRegistry());
        adminOnlyResources.setComponent(ComponentRegistry.class.getName(), componentRegistry);
        adminOnlyResources.setComponent(CacheRegistry.class.getName(), cacheRegistry);
        adminOnlyResources.setComponent(ComponentManager.class.getName(), componentManager);
        adminOnlyResources.setComponent(Constants.ADMIN_SERVICE_METADATA_REGISTRY, adminServiceMetadataManager);
        adminOnlyResources.setComponent(ServiceMetadataRegistry.class.getName(), serviceMetadataManager);
        adminOnlyResources.setComponent(Constants.CODEC_FACTORY_REGISTRY, codecFactoryRegistry);

        // resource declaration overrides for administration related services
        ConfigProvider<DependencyDeclarationOverride> rdoConfigProvider = runtimeConfig.getAdminDependencyDeclarationOverrideConfigProvider();
        rdoConfigProvider.setConfigReader(configReaderBuilder(runtimeConfig.getConfigFormat()).build());

        ServiceExecutorFactory adminServiceExecutorFactory = new ServiceExecutorFactory(
                adminOnlyResources,
                abs.adminServiceExecutorBindings.actorTypes(),
                rdoConfigProvider
        );
        // } stitch administration service runtime core


        // 5.{ authentication and authorization related
        AuthControlRegistry authControlRegistry = new AuthControlRegistry(
                new ServiceMetadataRegistryChain(
                        new ServiceMetadataRegistry[]{
                                adminServiceMetadataManager,
                                serviceMetadataManager
                        }
                )
        );

        ActorRegistry<AuthHandler> authHandlerRegistry = new ActorFactory<>(abs.authHandlerBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);
        for (NamedType<AuthControl> authControlNamedType : abs.authHandlerBindings.controlTypes().all()) {
            AuthHandler<?> authHandler = authHandlerRegistry.get(authControlNamedType.name());
            authHandler.setComponentRegistry(componentRegistry);
        }
        // } authentication and authorization related

        // 6.expose those needed for request serving
        RuntimeComponentsImpl components = new RuntimeComponentsImpl();

        components.setAdminServiceMetadataRegistry(adminServiceMetadataManager);
        components.setAdminServiceExecutorFactory(adminServiceExecutorFactory);
        components.setServiceMetadataRegistry(serviceMetadataManager);
        components.setServiceExecutorFactory(serviceExecutorFactory);
        components.setParameterProcessor(parameterProcessor);
        components.setCacheRegistry(cacheRegistry);
        components.setCacheKeyGeneratorRegistry(cacheKeyGeneratorRegistry);

        components.setAuthControlRegistry(authControlRegistry);
        components.setAuthHandlerRegistry(authHandlerRegistry);

        components.setInitializer(
                new InitializerComposite(
                        Arrays.asList(
                                componentManager,
                                adminServiceExecutorFactory,
                                adminServiceMetadataManager,
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

    private ActorBindingsSet loadAndInitActorBindings(List<ActorBindingsConfig> abcList) throws Exception {
        Map<String, ActorBindingsConfig> abcByScenario = abcList
                .stream()
                .collect(Collectors.toMap(ActorBindingsConfig::getScenario, abc -> abc));

        ActorBindingsSet abs = new ActorBindingsSet();

        // { CacheControl, CacheFactory, Cache }
        abs.cacheFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("cache.factory.types").getExtensionTypes(),
                CacheControl.class,
                Cache.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { KeyControl, KeyGenerator, Object }
        abs.cacheKeyGeneratorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("cache.key.generator.types").getExtensionTypes(),
                KeyControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ComponentConfig, ComponentFactory, DataClient }
        abs.componentFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("component.factory.types").getExtensionTypes(),
                ComponentConfig.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { Validation, Validator, Messages }
        abs.validatorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("param.validator.types").getExtensionTypes(),
                Validation.class,
                Messages.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { DeriveControl, Deriver, ParamValueHolder }
        abs.deriverBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("param.deriver.types").getExtensionTypes(),
                DeriveControl.class,
                ParamValueHolder.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { CodecControl, CodecFactory, Codec }
        abs.codecFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("codec.factory.types").getExtensionTypes(),
                CodecControl.class,
                Codec.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ValueFuncControl, ValueFuncFactory, ValueFunc }
        abs.valueFuncFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("value.func.factory.types").getExtensionTypes(),
                ValueFuncControl.class,
                ValueFunc.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { AuthControl, AuthHandler, Auth }
        abs.authHandlerBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("auth.handler.types").getExtensionTypes(),
                AuthControl.class,
                Auth.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ExecutionControl, ServiceExecutor, Object } for non-admin related service
        abs.serviceExecutorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("service.executor.types").getExtensionTypes(),
                ExecutionControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ExecutionControl, ServiceExecutor, Object } for administration purpose
        abs.adminServiceExecutorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("admin.service.executor.types").getExtensionTypes(),
                ExecutionControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        return abs;
    }

    private ObjectMapperBuilder configReaderBuilder(ConfigFormat configFormat) {
        if (configFormat == ConfigFormat.Yaml) {
            return ObjectMapperBuilder.forYAML();
        } else {
            return ObjectMapperBuilder.forJson();
        }
    }

    private ObjectMapper createServiceMetadataReader(
            ConfigFormat configFormat,
            NamedTypes<ExecutionControl> executionControlTypes,
            NamedTypes<CacheControl> cacheControlTypes,
            NamedTypes<KeyControl> keyControlTypes,
            NamedTypes<Validation> validationTypes,
            NamedTypes<DeriveControl> deriveControlTypes,
            NamedTypes<AuthControl> authControlTypes) {

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

        BaseTypeDeserializer<Validation> validationBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        Validation.class,
                        "type",
                        validationTypes.all()
                );

        BaseTypeDeserializer<DeriveControl> deriveControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        DeriveControl.class,
                        "type",
                        deriveControlTypes.all()
                );

        BaseTypeDeserializer<AuthControl> authControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthControl.class,
                        "type",
                        authControlTypes.all()
                );

        return configReaderBuilder(configFormat)
                .with(executionControlTypeDeserializer)
                .with(cacheControlBaseTypeDeserializer)
                .with(keyControlBaseTypeDeserializer)
                .with(validationBaseTypeDeserializer)
                .with(deriveControlBaseTypeDeserializer)
                .with(authControlBaseTypeDeserializer)
                .build();
    }

}
