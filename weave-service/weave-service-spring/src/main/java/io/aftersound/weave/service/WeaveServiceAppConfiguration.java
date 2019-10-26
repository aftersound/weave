package io.aftersound.weave.service;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.batch.jobspec.JobSpecRegistry;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.resources.ManagedResources;
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

        // JobSpec and JobRunner
        // TODO

        components.adminServiceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                components.adminExecutionControlTypes,
                components.cacheControlTypes,
                components.paramDeriveControlTypes
        );

        components.serviceMetadataReader = AppConfigUtils.createServiceMetadataReader(
                components.executionControlTypes,
                components.cacheControlTypes,
                components.paramDeriveControlTypes
        );

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

        DataClientManager dataClientManager = new DataClientManager(
                ObjectMapperBuilder.forJson().build(),
                PathHandle.of(properties.getServiceMetadataDirectory()).path(),
                components.dataClientRegistry
        );
        dataClientManager.init();
        adminOnlyResources.setResource(DataClientManager.class.getName(), dataClientManager);

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

    // end: common across admin-related and non-admin-related

    // start: administration services related

    @Bean
    @Qualifier("adminServiceMetadataManager")
    protected ServiceMetadataManager adminServiceMetadataManager() {
        Path metadataDirectory = PathHandle.of(properties.getAdminServiceMetadataDirectory()).path();
        AdminServiceMetadataManager serviceMetadataManager = new AdminServiceMetadataManager(
                components.adminServiceMetadataReader,
                metadataDirectory);
        serviceMetadataManager.init();
        return serviceMetadataManager;
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
