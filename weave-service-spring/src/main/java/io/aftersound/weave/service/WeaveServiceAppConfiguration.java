package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.cache.KeyControl;
import io.aftersound.weave.cache.KeyGenerator;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatControl;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.security.Authentication;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import io.aftersound.weave.security.Authorization;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.security.Authorizer;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.security.SecurityControlRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

@Configuration
@EnableMBeanExport
public class WeaveServiceAppConfiguration {

    private static final boolean TOLERATE_EXCEPTION = true;
    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    @Autowired
    WeaveServiceProperties properties;

    @Bean
    @Qualifier("components")
    protected ComponentBag initialize() throws Exception {

        // 1.{ load and init ActorBindings of service extension points
        ActorBindingsSet abs = loadAndInitAllRequiredActorBindings();
        // } load and init ActorBindings of service extension points


        // 2.{ create and stitch to form data client management runtime core
        DataClientRegistry dataClientRegistry = new DataClientRegistry(abs.dataClientFactoryBindings);
        DataClientManager dataClientManager = new DataClientManager(
                ObjectMapperBuilder.forJson().build(),
                PathHandle.of(properties.getServiceMetadataDirectory()).path(),
                dataClientRegistry
        );
        dataClientManager.init();
        // } create and stitch to form data client management runtime core


        // 3.{ create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry = new ActorFactory<>(abs.cacheKeyGeneratorBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<Deriver> paramDeriverRegistry = new ActorFactory<>(abs.deriverBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ActorRegistry<DataFormat> dataFormatRegistry = new ActorFactory<>(abs.dataFormatBindings)
                .createActorRegistryFromBindings(TOLERATE_EXCEPTION);

        ObjectMapper serviceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                abs.serviceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
                abs.deriverBindings.controlTypes(),
                abs.authenticatorBindings.controlTypes(),
                abs.authorizerBindings.controlTypes()
        );

        WeaveServiceMetadataManager serviceMetadataManager = new WeaveServiceMetadataManager(
                serviceMetadataReader,
                PathHandle.of(properties.getServiceMetadataDirectory()).path()
        );
        serviceMetadataManager.init();

        ManagedResources managedResources = new ManagedResourcesImpl();

        // make dataFormatRegistry available to non-admin/normal services
        managedResources.setResource("DataFormatRegistry", dataFormatRegistry);

        // make dataClientRegistry available to non-admin/normal services
        managedResources.setResource(DataClientRegistry.class.getName(), dataClientRegistry);

        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(managedResources);
        serviceExecutorFactory.init(abs.serviceExecutorBindings.actorTypes());

        // } create and stitch to form service execution runtime core


        // 4.{ stitch administration service runtime core
        ObjectMapper adminServiceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                abs.adminServiceExecutorBindings.controlTypes(),
                abs.cacheFactoryBindings.controlTypes(),
                abs.cacheKeyGeneratorBindings.controlTypes(),
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
        adminOnlyResources.setResource(DataClientRegistry.class.getName(), dataClientRegistry);
        adminOnlyResources.setResource(CacheRegistry.class.getName(), cacheRegistry);
        adminOnlyResources.setResource(DataClientManager.class.getName(), dataClientManager);
        adminOnlyResources.setResource(ServiceMetadataRegistry.class.getName(), serviceMetadataManager);

        ServiceExecutorFactory adminServiceExecutorFactory = new ServiceExecutorFactory(adminOnlyResources);
        adminServiceExecutorFactory.init(abs.adminServiceExecutorBindings.actorTypes());
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
        components.paramDeriverRegistry = paramDeriverRegistry;
        components.cacheRegistry = cacheRegistry;
        components.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;

        components.securityControlRegistry = securityControlRegistry;
        components.authenticatorRegistry = authenticatorRegistry;
        components.authorizerRegistry = authorizerRegistry;

        return components;
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
        abs.dataClientFactoryBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getDataClientFactoryTypesJson(),
                Endpoint.class,
                Object.class,
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

        // { ExecutionControl, ServiceExecutor, Object } for normal purpose
        abs.serviceExecutorBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getServiceExecutorTypesJson(),
                ExecutionControl.class,
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

    @Autowired
    @Qualifier("components")
    ComponentBag components;

    @Bean
    protected ActorRegistry<Deriver> paramDeriverRegistry() {
        return components.paramDeriverRegistry;
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
