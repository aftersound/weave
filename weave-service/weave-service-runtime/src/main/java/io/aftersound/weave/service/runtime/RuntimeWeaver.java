package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatControl;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.resource.ManagedResources;
import io.aftersound.weave.resource.ResourceConfig;
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
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.security.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RuntimeWeaver {

    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    public RuntimeWeaver() {
    }

    public RuntimeComponents initAndWeave(RuntimeConfig runtimeConfig) throws Exception {

        // 1.{ load and init ActorBindings of service extension points
        ConfigProvider<ActorBindingsConfig> actorBindingsConfigProvider = runtimeConfig.getActorBindingsConfigProvider();
        actorBindingsConfigProvider.setConfigReader(configReaderBuilder(runtimeConfig.getConfigFormat()).build());
        List<ActorBindingsConfig> actorBindingsConfigList = actorBindingsConfigProvider.getConfigList();
        ActorBindingsSet abs = loadAndInitActorBindings(actorBindingsConfigList);
        // } load and init ActorBindings of service extension points


        // 2.{ create and stitch to form client management runtime core
        ConfigProvider<Endpoint> clientConfigProvider = runtimeConfig.getClientConfigProvider();
        clientConfigProvider.setConfigReader(configReaderBuilder(runtimeConfig.getConfigFormat()).build());
        ClientRegistry clientRegistry = new ClientRegistry(abs.clientFactoryBindings);
        ClientManager clientManager = new ClientManager(
                "public",
                runtimeConfig.getClientConfigProvider(),
                runtimeConfig.getConfigUpdateStrategy(),
                clientRegistry
        );
        clientManager.loadConfigs(DO_NOT_TOLERATE_EXCEPTION);
        clientManager.init();
        // } create and stitch to form client management runtime core


        // 3.{ create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry = new ActorFactory<>(abs.cacheKeyGeneratorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<Validator> paramValidatorRegistry = new ActorFactory<>(abs.validatorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<Deriver> paramDeriverRegistry = new ActorFactory<>(abs.deriverBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<DataFormat> dataFormatRegistry = new ActorFactory<>(abs.dataFormatBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ObjectMapper serviceMetadataReader = createServiceMetadataReader(
                runtimeConfig.getConfigFormat(),
                abs.serviceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.validatorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authenticatorBindings.controlTypes(),
                abs.authorizerBindings.controlTypes()
        );

        ConfigProvider<ServiceMetadata> serviceMetadataProvider = runtimeConfig.getServiceMetadataProvider();
        serviceMetadataProvider.setConfigReader(serviceMetadataReader);
        ServiceMetadataManager serviceMetadataManager = new ServiceMetadataManager(
                "public",
                serviceMetadataProvider,
                runtimeConfig.getConfigUpdateStrategy(),
                cacheRegistry
        );
        serviceMetadataManager.loadConfigs(DO_NOT_TOLERATE_EXCEPTION);
        serviceMetadataManager.init();

        ManagedResources managedResources = new ManagedResourcesImpl();

        // make dataFormatRegistry available to non-admin/normal services
        managedResources.setResource("DataFormatRegistry", dataFormatRegistry);

        // make dataClientRegistry available to non-admin/normal services
        managedResources.setResource(ClientRegistry.class.getName(), clientRegistry);

        ObjectMapper resourceConfigReader = configReaderBuilder(runtimeConfig.getConfigFormat())
                .with(
                        new BaseTypeDeserializer<>(
                                ResourceConfig.class,
                                "type",
                                abs.resourceManagerBindings.controlTypes().all()
                        )
                )
                .build();
        ConfigProvider<ResourceConfig> resourceConfigProvider = runtimeConfig.getResourceConfigProvider();
        resourceConfigProvider.setConfigReader(resourceConfigReader);
        List<ResourceConfig> resourceConfigList = resourceConfigProvider.getConfigList();
        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(managedResources);
        serviceExecutorFactory.init(abs.serviceExecutorBindings.actorTypes(), resourceConfigList);

        ParameterProcessor<HttpServletRequest> parameterProcessor = runtimeConfig.getParameterProcessor(
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
                abs.authenticatorBindings.controlTypes(),
                abs.authorizerBindings.controlTypes()
        );
        ConfigProvider<ServiceMetadata> adminServiceMetadataProvider = runtimeConfig.getAdminServiceMetadataProvider();
        adminServiceMetadataProvider.setConfigReader(adminServiceMetadataReader);
        ServiceMetadataManager adminServiceMetadataManager = new ServiceMetadataManager(
                "protected",
                adminServiceMetadataProvider,
                ConfigUpdateStrategy.ondemand(),
                null
        );
        adminServiceMetadataManager.loadConfigs(DO_NOT_TOLERATE_EXCEPTION);

        // make following beans available to administration services
        // for admin purpose only
        ManagedResources adminOnlyResources = new ManagedResourcesImpl();

        adminOnlyResources.setResource(Constants.SERVICE_METADATA_READER, serviceMetadataReader);
        adminOnlyResources.setResource(ClientRegistry.class.getName(), clientRegistry);
        adminOnlyResources.setResource(CacheRegistry.class.getName(), cacheRegistry);
        adminOnlyResources.setResource(ClientManager.class.getName(), clientManager);
        adminOnlyResources.setResource(ServiceMetadataRegistry.class.getName(), serviceMetadataManager);

        ObjectMapper adminResourceConfigReader = configReaderBuilder(runtimeConfig.getConfigFormat())
                .with(
                        new BaseTypeDeserializer<>(
                                ResourceConfig.class,
                                "type",
                                abs.adminResourceManagerBindings.controlTypes().all()
                        )
                )
                .build();
        ConfigProvider<ResourceConfig> adminResourceConfigProvider = runtimeConfig.getAdminResourceConfigProvider();
        adminResourceConfigProvider.setConfigReader(adminResourceConfigReader);
        List<ResourceConfig> adminResourceConfigList = adminResourceConfigProvider.getConfigList();
        ServiceExecutorFactory adminServiceExecutorFactory = new ServiceExecutorFactory(adminOnlyResources);
        adminServiceExecutorFactory.init(abs.adminServiceExecutorBindings.actorTypes(), adminResourceConfigList);
        // } stitch administration service runtime core


        // 5.{ authentication and authorization related
        SecurityControlRegistry securityControlRegistry = new SecurityControlRegistry(
                new ServiceMetadataRegistryChain(
                        new ServiceMetadataRegistry[]{
                                adminServiceMetadataManager,
                                serviceMetadataManager
                        }
                )
        );

        ActorRegistry<Authenticator> authenticatorRegistry = new ActorFactory<>(abs.authenticatorBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);

        ActorRegistry<Authorizer> authorizerRegistry = new ActorFactory<>(abs.authorizerBindings)
                .createActorRegistryFromBindings(DO_NOT_TOLERATE_EXCEPTION);
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

        components.setSecurityControlRegistry(securityControlRegistry);
        components.setAuthenticatorRegistry(authenticatorRegistry);
        components.setAuthorizerRegistry(authorizerRegistry);

        components.setManagementFacades(new ManagementFacadesImpl(clientManager, serviceMetadataManager));

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

        // { Endpoint, DataClientFactory, DataClient }
        abs.clientFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("client.factory.types").getExtensionTypes(),
                Endpoint.class,
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

        // { DataFormatControl, DataFormat, Serializer/Deserializer }
        abs.dataFormatBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("data.format.types").getExtensionTypes(),
                DataFormatControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { AuthenticationControl, Authenticator, Authentication }
        abs.authenticatorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("authenticator.types").getExtensionTypes(),
                AuthenticationControl.class,
                Authentication.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { AuthorizationControl, Authorizer, Authorization }
        abs.authorizerBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("authorizer.types").getExtensionTypes(),
                AuthorizationControl.class,
                Authorization.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ResourceConfig, ResourceManager, RESOURCE } for non-admin related purpose
        abs.resourceManagerBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("resource.manager.types").getExtensionTypes(),
                ResourceConfig.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ExecutionControl, ServiceExecutor, Object } for non-admin related service
        abs.serviceExecutorBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("service.executor.types").getExtensionTypes(),
                ExecutionControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ResourceConfig, ResourceManager, RESOURCE } for admin related purpose
        abs.adminResourceManagerBindings = ActorBindingsUtil.loadActorBindings(
                abcByScenario.get("admin.resource.manager.types").getExtensionTypes(),
                ResourceConfig.class,
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
            NamedTypes<AuthenticationControl> authenticationControlTypes,
            NamedTypes<AuthorizationControl> authorizationControlTypes) {

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

        BaseTypeDeserializer<AuthenticationControl> authenticationControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthenticationControl.class,
                        "type",
                        authenticationControlTypes.all()
                );

        BaseTypeDeserializer<AuthorizationControl> authorizationControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthorizationControl.class,
                        "type",
                        authorizationControlTypes.all()
                );

        return configReaderBuilder(configFormat)
                .with(executionControlTypeDeserializer)
                .with(cacheControlBaseTypeDeserializer)
                .with(keyControlBaseTypeDeserializer)
                .with(validationBaseTypeDeserializer)
                .with(deriveControlBaseTypeDeserializer)
                .with(authenticationControlBaseTypeDeserializer)
                .with(authorizationControlBaseTypeDeserializer)
                .build();
    }
}