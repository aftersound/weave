package io.aftersound.weave.service;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatRegistry;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.security.*;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.service.security.SecurityControlRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import java.nio.file.Path;

@Configuration
@EnableMBeanExport
public class WeaveServiceAppConfiguration {

    private static final boolean TOLERATE_EXCEPTION = true;
    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    @Autowired
    WeaveServiceProperties properties;

    // for admin purpose only
    private ManagedResources adminOnlyResources = new ManagedResourcesImpl();

    // for normal services
    private ManagedResources managedResources = new ManagedResourcesImpl();

    // start: common across admin-related and non-admin-related
    @Bean
    @Qualifier("components")
    protected ComponentBag components() throws Exception {
        ComponentBag components = new ComponentBag();

        // CacheControl, CacheFactory and Cache
        ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getCacheFactoryTypesJson(),
                        CacheControl.class,
                        Cache.class,
                        TOLERATE_EXCEPTION
                );
        components.cacheControlTypes = cacheFactoryBindings.controlTypes();
        components.cacheRegistry = new CacheRegistry(cacheFactoryBindings);

        // Endpoint, DataClientFactory and Data Client
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getDataClientFactoryTypesJson(),
                        Endpoint.class,
                        Object.class,
                        TOLERATE_EXCEPTION
                );
        components.dataClientRegistry = new DataClientRegistry(dataClientFactoryBindings);

        // DeriveControl and Deriver
        ActorBindings<DeriveControl, Deriver, ParamValueHolder> deriverBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getParamDeriverTypesJson(),
                        DeriveControl.class,
                        ParamValueHolder.class,
                        TOLERATE_EXCEPTION
                );
        components.paramDeriveControlTypes = deriverBindings.controlTypes();
        components.paramDeriverFactory = new ActorFactory<>(deriverBindings);

        // DataFormat
        ActorBindings<String, DataFormat, Object> dataFormatBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getDataFormatTypesJson(),
                        String.class,
                        Object.class,
                        TOLERATE_EXCEPTION
                );
        components.dataFormatRegistry = new DataFormatRegistry().initialize(dataFormatBindings.actorTypes());

        // ExecutionControl and ServiceExecutor for administration purpose
        ActorBindings<ExecutionControl, ServiceExecutor, Object> adminServiceExecutorBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getAdminServiceExecutorTypesJson(),
                        ExecutionControl.class,
                        Object.class,
                        DO_NOT_TOLERATE_EXCEPTION
                );
        components.adminServiceExecutorTypes = adminServiceExecutorBindings.actorTypes();
        components.adminExecutionControlTypes = adminServiceExecutorBindings.controlTypes();

        // ExecutionControl and ServiceExecutor for normal purpose
        ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getServiceExecutorTypesJson(),
                        ExecutionControl.class,
                        Object.class,
                        TOLERATE_EXCEPTION
                );
        components.serviceExecutorTypes = serviceExecutorBindings.actorTypes();
        components.executionControlTypes = serviceExecutorBindings.controlTypes();

        // AuthenticationControl and Authenticator
        ActorBindings<AuthenticationControl, Authenticator, Authentication> authenticatorBinding =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getAuthenticatorTypesJson(),
                        AuthenticationControl.class,
                        Authentication.class,
                        TOLERATE_EXCEPTION
                );
        components.authenticatorTypes = authenticatorBinding.actorTypes();
        components.authenticationControlTypes = authenticatorBinding.controlTypes();

        // AuthorizationControl and Authorizer
        ActorBindings<AuthorizationControl, Authorizer, Authorization> authorizerBinding =
                AppConfigUtils.loadAndInitActorBindings(
                        properties.getAuthorizerTypesJson(),
                        AuthorizationControl.class,
                        Authorization.class,
                        TOLERATE_EXCEPTION
                );
        components.authorizerTypes = authorizerBinding.actorTypes();
        components.authorizationControlTypes = authorizerBinding.controlTypes();

        components.adminServiceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                components.adminExecutionControlTypes,
                components.cacheControlTypes,
                components.paramDeriveControlTypes,
                components.authenticationControlTypes,
                components.authorizationControlTypes
        );

        components.serviceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                components.executionControlTypes,
                components.cacheControlTypes,
                components.paramDeriveControlTypes,
                components.authenticationControlTypes,
                components.authorizationControlTypes
        );

        // JobSpec and JobWorker
        // TODO

        components.jobSpecReader = ObjectMapperBuilder.forJson().build();   // DUMMY now

        // make serviceMetadataReader available to administration services
        adminOnlyResources.setResource(Constants.SERVICE_METADATA_READER, components.serviceMetadataReader);

        // make dataClientRegistry available to administration services
        adminOnlyResources.setResource(DataClientRegistry.class.getName(), components.dataClientRegistry);

        // make dataClientRegistry available to non-admin/normal services
        managedResources.setResource(DataClientRegistry.class.getName(), components.dataClientRegistry);

        // make cacheRegistry available to administration services
        adminOnlyResources.setResource(CacheRegistry.class.getName(), CacheRegistry.class);

        // make cacheRegistry available to non-admin/normal services
        managedResources.setResource(CacheRegistry.class.getName(), CacheRegistry.class);

        // make dataFormatRegistry available to non-admin/normal services
        managedResources.setResource(DataFormatRegistry.class.getName(), components.dataFormatRegistry);

        DataClientManager dataClientManager = new DataClientManager(
                ObjectMapperBuilder.forJson().build(),
                PathHandle.of(properties.getServiceMetadataDirectory()).path(),
                components.dataClientRegistry
        );
        dataClientManager.init();
        adminOnlyResources.setResource(DataClientManager.class.getName(), dataClientManager);

        Path metadataDirectory = PathHandle.of(properties.getAdminServiceMetadataDirectory()).path();
        AdminServiceMetadataManager adminServiceMetadataManager = new AdminServiceMetadataManager(
                components.adminServiceMetadataReader,
                metadataDirectory
        );
        adminServiceMetadataManager.init();
        components.adminServiceMetadataManager = adminServiceMetadataManager;

        WeaveServiceMetadataManager serviceMetadataManager = new WeaveServiceMetadataManager(
                components.serviceMetadataReader,
                PathHandle.of(properties.getServiceMetadataDirectory()).path()
        );
        serviceMetadataManager.init();
        components.serviceMetadataManager = serviceMetadataManager;
        adminOnlyResources.setResource(ServiceMetadataRegistry.class.getName(), serviceMetadataManager);

        WeaveJobSpecManager jobSpecManager = new WeaveJobSpecManager(
                components.jobSpecReader,
                PathHandle.of(properties.getJobSpecDirectory()).path()
        );
        // jobSpecManager.init();
        adminOnlyResources.setResource(JobSpecRegistry.class.getName(), jobSpecManager);

        // SecurityControlRegistry
        components.securityControlRegistry = new SecurityControlRegistry(
                new ServiceMetadataRegistryChain(
                        new ServiceMetadataRegistry[]{
                                components.adminServiceMetadataManager,
                                components.serviceMetadataManager
                        }
                )
        );

        return components;
    }

    @Autowired
    @Qualifier("components")
    ComponentBag components;

    @Bean
    @Qualifier("paramDeriverFactory")
    protected ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory() {
        return components.paramDeriverFactory;
    }

    @Bean
    protected SecurityControlRegistry securityControlRegistry() {
        return components.securityControlRegistry;
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
    protected ServiceExecutorFactory adminServiceExecutorFactory() throws Exception {
        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(adminOnlyResources);
        serviceExecutorFactory.init(components.adminServiceExecutorTypes);
        return serviceExecutorFactory;
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
    protected ServiceExecutorFactory serviceExecutorFactory() throws Exception {
        ServiceExecutorFactory serviceExecutorFactory = new ServiceExecutorFactory(managedResources);
        serviceExecutorFactory.init(components.serviceExecutorTypes);
        return serviceExecutorFactory;
    }

    // end: normal service related

}
