package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.cache.*;
import io.aftersound.weave.data.DataFormatRegistry;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.security.Authentication;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authorization;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamDeriverRegistry;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.security.AuthenticatorFactory;
import io.aftersound.weave.service.security.AuthorizerFactory;
import io.aftersound.weave.service.security.SecurityControlRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import java.util.Map;

@Configuration
@EnableMBeanExport
public class WeaveServiceAppConfiguration {

    private static final boolean TOLERATE_EXCEPTION = true;
    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    @Autowired
    WeaveServiceProperties properties;

    // start: common across admin-related and non-admin-related
    @Bean
    @Qualifier("components")
    protected ComponentBag initialize() throws Exception {

        // { load and init ActorBindings of service extension points
        ActorBindingsSet abs = loadAndInitAllRequiredActorBindings();
        // } load and init ActorBindings of service extension points


        // { create and stitch to form data client management runtime core
        DataClientRegistry dataClientRegistry = new DataClientRegistry(abs.dataClientFactoryBindings);
        DataClientManager dataClientManager = new DataClientManager(
                ObjectMapperBuilder.forJson().build(),
                PathHandle.of(properties.getServiceMetadataDirectory()).path(),
                dataClientRegistry
        );
        dataClientManager.init();
        // } create and stitch to form data client management runtime core


        // { create and stitch to form service execution runtime core
        CacheRegistry cacheRegistry = new CacheRegistry(abs.cacheFactoryBindings);

        ActorFactory<KeyControl, KeyGenerator, Object> keyGeneratorFactory =
                new ActorFactory<>(abs.cacheKeyGeneratorBindings);
        Map<String, KeyGenerator> keyGenerators = keyGeneratorFactory.createActorsFromBindings(TOLERATE_EXCEPTION);
        KeyGeneratorRegistry cacheKeyGeneratorRegistry = new KeyGeneratorRegistry(keyGenerators);

        ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory =
                new ActorFactory<>(abs.deriverBindings);
        Map<String, Deriver> paramDerivers = paramDeriverFactory.createActorsFromBindings(TOLERATE_EXCEPTION);
        ParamDeriverRegistry paramDeriverRegistry = new ParamDeriverRegistry(paramDerivers);

        DataFormatRegistry dataFormatRegistry =
                new DataFormatRegistry().initialize(abs.dataFormatBindings.actorTypes());

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
        managedResources.setResource(DataFormatRegistry.class.getName(), dataFormatRegistry);

        // make dataClientRegistry available to non-admin/normal services
        managedResources.setResource(DataClientRegistry.class.getName(), dataClientRegistry);

        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(managedResources);
        serviceExecutorFactory.init(abs.serviceExecutorBindings.actorTypes());

        // } create and stitch to form service execution runtime core


        // { stitch administration service runtime core
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


        // { authentication and authorization related
        SecurityControlRegistry securityControlRegistry = new SecurityControlRegistry(
                new ServiceMetadataRegistryChain(
                        new ServiceMetadataRegistry[]{
                                adminServiceMetadataManager,
                                serviceMetadataManager
                        }
                )
        );

        AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(abs.authenticatorBindings);
        AuthorizerFactory authorizerFactory = new AuthorizerFactory(abs.authorizerBindings);
        // } authentication and authorization related

        // expose those need to be exposed for component autowiring
        ComponentBag components = new ComponentBag();

        components.adminServiceMetadataManager = adminServiceMetadataManager;
        components.adminServiceExecutorFactory = adminServiceExecutorFactory;

        components.serviceMetadataManager = serviceMetadataManager;
        components.serviceExecutorFactory = serviceExecutorFactory;
        components.paramDeriverRegistry = paramDeriverRegistry;
        components.cacheRegistry = cacheRegistry;
        components.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;

        components.securityControlRegistry = securityControlRegistry;
        components.authenticatorFactory = authenticatorFactory;
        components.authorizerFactory = authorizerFactory;

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

        // { Void, DataFormat, Serializer/Deserializer }
        abs.dataFormatBindings = AppConfigUtils.loadAndInitActorBindings(
                properties.getDataFormatTypesJson(),
                String.class,
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

    // start: common across admin-related and non-admin-related

    @Bean
    protected ParamDeriverRegistry paramDeriverRegistry() {
        return components.paramDeriverRegistry;
    }

    @Bean
    protected CacheRegistry cacheRegistry() {
        return components.cacheRegistry;
    }

    @Bean
    protected KeyGeneratorRegistry cacheKeyGeneratorRegistry() {
        return components.cacheKeyGeneratorRegistry;
    }

    @Bean
    protected SecurityControlRegistry securityControlRegistry() {
        return components.securityControlRegistry;
    }

    @Bean
    protected AuthenticatorFactory authenticatorFactory() {
        return components.authenticatorFactory;
    }

    @Bean
    protected AuthorizerFactory authorizerFactory() {
        return components.authorizerFactory;
    }

    // end: common across admin-related and non-admin-related

    // start: administration services related

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

    // end: administration services related

    // start: normal services related

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

    // end: normal service related

}
