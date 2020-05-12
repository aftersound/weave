package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatControl;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.resource.ManagedResources;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.Validation;
import io.aftersound.weave.service.request.*;
import io.aftersound.weave.service.security.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Configuration
@EnableMBeanExport
public class WeaveServiceAppConfiguration {

    private static final boolean TOLERATE_EXCEPTION = true;
    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    private final WeaveServiceProperties properties;

    private ComponentBag components;

    public WeaveServiceAppConfiguration(WeaveServiceProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    protected void initialize() throws Exception {

        // 1.{ load and init ActorBindings of service extension points
        ActorBindingsSet abs = loadAndInitAllRequiredActorBindings();
        // } load and init ActorBindings of service extension points


        // 2.{ create and stitch to form client management runtime core
        ClientRegistry clientRegistry = new ClientRegistry(abs.clientFactoryBindings);
        ClientManager clientManager = new ClientManager(
                ObjectMapperBuilder.forJson().build(),
                PathHandle.of(properties.getDataClientConfigDirectory()).path(),
                clientRegistry
        );
        clientManager.init();
        // } create and stitch to form data client management runtime core


        // 3.{ create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry = new ActorFactory<>(abs.cacheKeyGeneratorBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<Validator> paramValidatorRegistry = new ActorFactory<>(abs.validatorBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<Deriver> paramDeriverRegistry = new ActorFactory<>(abs.deriverBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<DataFormat> dataFormatRegistry = new ActorFactory<>(abs.dataFormatBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ObjectMapper serviceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                abs.serviceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.validatorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authenticatorBindings.controlTypes(),
                abs.authorizerBindings.controlTypes()
        );

        WeaveServiceMetadataManager serviceMetadataManager = new WeaveServiceMetadataManager(
                serviceMetadataReader,
                PathHandle.of(properties.getServiceMetadataDirectory()).path(),
                cacheRegistry
        );
        serviceMetadataManager.init();

        ManagedResources managedResources = new ManagedResourcesImpl();

        // make dataFormatRegistry available to non-admin/normal services
        managedResources.setResource("DataFormatRegistry", dataFormatRegistry);

        // make dataClientRegistry available to non-admin/normal services
        managedResources.setResource(ClientRegistry.class.getName(), clientRegistry);

        ObjectMapper resourceConfigReader = ObjectMapperBuilder.forJson()
                .with(
                        new BaseTypeDeserializer<>(
                                ResourceConfig.class,
                                "type",
                                abs.resourceManagerBindings.controlTypes().all()
                        )
                )
                .build();
        List<ResourceConfig> resourceConfigList = new ManagedResourceConfigLoader(
                resourceConfigReader,
                PathHandle.of(properties.getResourceConfigDirectory()).path()
        ).load();
        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(managedResources);
        serviceExecutorFactory.init(abs.serviceExecutorBindings.actorTypes(), resourceConfigList);

        ParameterProcessor<HttpServletRequest> parameterProcessor = new CoreParameterProcessor(
                paramValidatorRegistry,
                paramDeriverRegistry
        );

        // } create and stitch to form service execution runtime core


        // 4.{ stitch administration service runtime core
        ObjectMapper adminServiceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                abs.adminServiceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.validatorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authenticatorBindings.controlTypes(),
                abs.authorizerBindings.controlTypes()
        );
        AdminServiceMetadataManager adminServiceMetadataManager = new AdminServiceMetadataManager(
                adminServiceMetadataReader,
                PathHandle.of(properties.getAdminServiceMetadataDirectory()).path()
        );
        adminServiceMetadataManager.init();

        // make following beans available to administration services
        // for admin purpose only
        ManagedResources adminOnlyResources = new ManagedResourcesImpl();

        // TODO: why this is needed?
        WeaveJobSpecManager jobSpecManager = new WeaveJobSpecManager(
                ObjectMapperBuilder.forJson().build(),  // DUMMY now
                PathHandle.of(properties.getJobSpecDirectory()).path()
        );
        // jobSpecManager.init();
        adminOnlyResources.setResource(JobSpecRegistry.class.getName(), jobSpecManager);

        adminOnlyResources.setResource(Constants.SERVICE_METADATA_READER, serviceMetadataReader);
        adminOnlyResources.setResource(ClientRegistry.class.getName(), clientRegistry);
        adminOnlyResources.setResource(CacheRegistry.class.getName(), cacheRegistry);
        adminOnlyResources.setResource(ClientManager.class.getName(), clientManager);
        adminOnlyResources.setResource(ServiceMetadataRegistry.class.getName(), serviceMetadataManager);

        ObjectMapper adminResourceConfigReader = ObjectMapperBuilder.forJson()
                .with(
                        new BaseTypeDeserializer<>(
                                ResourceConfig.class,
                                "type",
                                abs.adminResourceManagerBindings.controlTypes().all()
                        )
                )
                .build();
        List<ResourceConfig> adminResourceConfigList = new ManagedResourceConfigLoader(
                adminResourceConfigReader,
                PathHandle.of(properties.getAdminResourceConfigDirectory()).path()
        ).load();
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
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<Authorizer> authorizerRegistry = new ActorFactory<>(abs.authorizerBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);
        // } authentication and authorization related

        // 6.expose those needed for request serving
        ComponentBag components = new ComponentBag();

        components.adminServiceMetadataManager = adminServiceMetadataManager;
        components.adminServiceExecutorFactory = adminServiceExecutorFactory;

        components.serviceMetadataManager = serviceMetadataManager;
        components.serviceExecutorFactory = serviceExecutorFactory;
        components.parameterProcessor = parameterProcessor;
        components.cacheRegistry = cacheRegistry;
        components.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;

        components.securityControlRegistry = securityControlRegistry;
        components.authenticatorRegistry = authenticatorRegistry;
        components.authorizerRegistry = authorizerRegistry;

        this.components = components;
    }

    private ActorBindingsSet loadAndInitAllRequiredActorBindings() throws Exception {
        ActorBindingsSet abs = new ActorBindingsSet();

        // { CacheControl, CacheFactory, Cache }
        abs.cacheFactoryBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getCacheFactoryTypesJson(),
                CacheControl.class,
                Cache.class,
                TOLERATE_EXCEPTION
        );

        // { KeyControl, KeyGenerator, Object }
        abs.cacheKeyGeneratorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getCacheKeyGeneratorTypesJson(),
                KeyControl.class,
                Object.class,
                TOLERATE_EXCEPTION
        );

        // { Endpoint, DataClientFactory, DataClient }
        abs.clientFactoryBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getDataClientFactoryTypesJson(),
                Endpoint.class,
                Object.class,
                TOLERATE_EXCEPTION
        );

        // { Validation, Validator, Messages }
        abs.validatorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getParamValidatorTypesJson(),
                Validation.class,
                Messages.class,
                TOLERATE_EXCEPTION
        );

        // { DeriveControl, Deriver, ParamValueHolder }
        abs.deriverBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getParamDeriverTypesJson(),
                DeriveControl.class,
                ParamValueHolder.class,
                TOLERATE_EXCEPTION
        );

        // { DataFormatControl, DataFormat, Serializer/Deserializer }
        abs.dataFormatBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getDataFormatTypesJson(),
                DataFormatControl.class,
                Object.class,
                TOLERATE_EXCEPTION
        );

        // { AuthenticationControl, Authenticator, Authentication }
        abs.authenticatorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getAuthenticatorTypesJson(),
                AuthenticationControl.class,
                Authentication.class,
                TOLERATE_EXCEPTION
        );

        // { AuthorizationControl, Authorizer, Authorization }
        abs.authorizerBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getAuthorizerTypesJson(),
                        AuthorizationControl.class,
                        Authorization.class,
                        TOLERATE_EXCEPTION
                );

        // { ResourceConfig, ResourceManager, RESOURCE } for non-admin related purpose
        abs.resourceManagerBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getResourceManagerTypesJson(),
                        ResourceConfig.class,
                        Object.class,
                        TOLERATE_EXCEPTION
                );

        // { ExecutionControl, ServiceExecutor, Object } for non-admin related service
        abs.serviceExecutorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getServiceExecutorTypesJson(),
                ExecutionControl.class,
                Object.class,
                TOLERATE_EXCEPTION
        );

        // { ResourceConfig, ResourceManager, RESOURCE } for admin related purpose
        abs.adminResourceManagerBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getAdminResourceManagerTypesJson(),
                        ResourceConfig.class,
                        Object.class,
                        TOLERATE_EXCEPTION
                );

        // { ExecutionControl, ServiceExecutor, Object } for administration purpose
        abs.adminServiceExecutorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getAdminServiceExecutorTypesJson(),
                ExecutionControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        return abs;
    }



    @Bean
    protected ParameterProcessor<HttpServletRequest> parameterProcessor() {
        return components.parameterProcessor;
    }

    @Bean
    protected CacheRegistry cacheRegistry() {
        return components.cacheRegistry;
    }

    @Bean
    protected ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry() {
        return components.cacheKeyGeneratorRegistry;
    }

    @Bean
    protected SecurityControlRegistry securityControlRegistry() {
        return components.securityControlRegistry;
    }

    @Bean
    protected ActorRegistry<Authenticator> authenticatorRegistry() {
        return components.authenticatorRegistry;
    }

    @Bean
    protected ActorRegistry<Authorizer> authorizerRegistry() {
        return components.authorizerRegistry;
    }

    @Bean
    @Qualifier("adminServiceMetadataManager")
    protected ServiceMetadataManager adminServiceMetadataManager() {
        return components.adminServiceMetadataManager;
    }

    @Bean
    @Qualifier("adminServiceExecutorFactory")
    protected ServiceExecutorFactory adminServiceExecutorFactory() {
        return components.adminServiceExecutorFactory;
    }

    @Bean
    @Qualifier("serviceMetadataManager")
    protected ServiceMetadataManager serviceMetadataManager() {
        return components.serviceMetadataManager;
    }

    @Bean
    @Qualifier("serviceExecutorFactory")
    protected ServiceExecutorFactory serviceExecutorFactory() {
        return components.serviceExecutorFactory;
    }

}
