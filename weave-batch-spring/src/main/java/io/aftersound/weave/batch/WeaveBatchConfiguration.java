package io.aftersound.weave.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.NamedTypeUtil;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.batch.jobspec.etl.transform.TransformControl;
import io.aftersound.weave.batch.worker.JobWorker;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.filehandler.*;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.resource.ManagedResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Map;

public class WeaveBatchConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveBatchConfiguration.class);

    @Configuration
    public static class BootstrapConfiguration {

        @Autowired
        private WeaveProperties weaveProperties;

        @Bean
        public AppConfig appConfig() throws Exception {
            LOGGER.info("Parse AppConfig from file[" + weaveProperties.getAppConfig() + "]...");
            AppConfig appConfig = new ObjectMapper().readValue(
                    new File(weaveProperties.getAppConfig()), AppConfig.class);
            LOGGER.info("Parse AppConfig from file[" + weaveProperties.getAppConfig() + "]...DONE!");
            return appConfig;
        }

    }

    @Configuration
    public static class SpringDataSourceConfiguration {

        private static final String DRIVER_CLASS_NAME = "spring.datasource.driver-class-name";
        private static final String URL = "spring.datasource.url";
        private static final String USER_NAME = "spring.datasource.username";
        private static final String PASSWORD = "spring.datasource.password";

        @Autowired
        private AppConfig appConfig;

        @Bean
        public DataSource dataSource() {
            LOGGER.info("Initialize DataSource for Spring...");

            Map<String, String> config = appConfig.getSpringDataSourceConfig();

            LOGGER.info(DRIVER_CLASS_NAME + "=" + config.get(DRIVER_CLASS_NAME));
            LOGGER.info(URL + "=" + config.get(URL));
            LOGGER.info(USER_NAME + "=********");
            LOGGER.info(PASSWORD + "=********");

            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(config.get(DRIVER_CLASS_NAME));
            dataSourceBuilder.url(config.get(URL));
            dataSourceBuilder.username(config.get(USER_NAME));
            dataSourceBuilder.password(config.get(PASSWORD));
            DataSource dataSource = dataSourceBuilder.build();

            LOGGER.info("Initialize DataSource for Spring...DONE!");

            return dataSource;
        }

    }

    @Configuration
    public static class ActorBindingsConfiguration {

        @Autowired
        private AppConfig appConfig;

        @Bean
        public ActorBindings<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterBindings()
                throws Exception {
            LOGGER.info("Initialize types of FileFilter(s) behaves in according to pairing FileFilterControl...");

            ActorBindings<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterBindings =
                    ActorBindingsUtil.loadActorBindings(
                            appConfig.getFileFilterTypes(),
                            FileFilterControl.class,
                            Object.class,
                            false
                    );

            LOGGER.info("Initialize types of FileFilter(s) behaves in according to pairing FileFilterControl...Done!");

            return fileFilterBindings;
        }

        @Bean
        public ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings()
                throws Exception {

            LOGGER.info("Initialize types of FileHandler(s) behaves in according to pairing FileHandlingControl...");

            ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings =
                    ActorBindingsUtil.loadActorBindings(
                            appConfig.getFileHandlerTypes(),
                            FileHandlingControl.class,
                            Object.class,
                            false
                    );

            LOGGER.info("Initialize types of FileHandler(s) behaves in according to pairing FileHandlingControl...Done!");

            return fileHandlerBindings;
        }

        @Bean
        public DataClientRegistry dataClientRegistry() throws Exception {
            LOGGER.info("Initialize types of DataClientFactory...");

            ActorBindings<Endpoint, DataClientFactory<?>, Object> dataClientFactoryBindings =
                    ActorBindingsUtil.loadActorBindings(
                            appConfig.getDataClientFactoryTypes(),
                            Endpoint.class,
                            Object.class,
                            false
                    );

            LOGGER.info("Initialize types of DataClientFactory...DONE!");

            return new DataClientRegistry(dataClientFactoryBindings);
        }

    }

    @Configuration
    public static class JobSpecAwareObjectMapperConfiguration {

        @Autowired
        private AppConfig appConfig;

        @Autowired
        private ActorBindings<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterBindings;

        @Autowired
        private ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings;

        @Bean
        public ObjectMapper jobSpecAwareObjectMapper() throws Exception {
            LOGGER.info("Initialize JobSpec deserializer...");

            final boolean tolerateIndividualException = false;

            NamedTypes<JobSpec> jobSpecNamedTypes = NamedTypeUtil.loadClassesAndExtractNamedTypes(
                    appConfig.getJsonSpecTypes(),
                    JobSpec.class,
                    "TYPE",
                    tolerateIndividualException
            );

            NamedTypes<DataSourceControl> dscNamedTypes = NamedTypeUtil.loadClassesAndExtractNamedTypes(
                    appConfig.getDataSourceControlTypes(),
                    DataSourceControl.class,
                    "TYPE",
                    tolerateIndividualException
            );

            NamedTypes<ExtractControl> ecNamedTypes = NamedTypeUtil.loadClassesAndExtractNamedTypes(
                    appConfig.getExtractControlTypes(),
                    ExtractControl.class,
                    "TYPE",
                    tolerateIndividualException
            );

            NamedTypes<TransformControl> tcNamedTypes = NamedTypeUtil.loadClassesAndExtractNamedTypes(
                    appConfig.getTransformControlTypes(),
                    TransformControl.class,
                    "TYPE",
                    tolerateIndividualException
            );

            NamedTypes<LoadControl> lcNamedTypes = NamedTypeUtil.loadClassesAndExtractNamedTypes(
                    appConfig.getLoadControlTypes(),
                    LoadControl.class,
                    "TYPE",
                    tolerateIndividualException
            );

            ObjectMapper objectMapper = ObjectMapperBuilder.forJson()
                    .with(baseTypeDeserializer(jobSpecNamedTypes, JobSpec.class))
                    .with(baseTypeDeserializer(dscNamedTypes, DataSourceControl.class))
                    .with(baseTypeDeserializer(ecNamedTypes, ExtractControl.class))
                    .with(baseTypeDeserializer(tcNamedTypes, TransformControl.class))
                    .with(baseTypeDeserializer(lcNamedTypes, LoadControl.class))
                    .with(baseTypeDeserializer(fileFilterBindings.controlTypes(), FileFilterControl.class))
                    .with(baseTypeDeserializer(fileHandlerBindings.controlTypes(), FileHandlingControl.class))
                    .build();

            LOGGER.info("Initialize JobSpec deserializer...DONE!");

            return objectMapper;
        }

        private static <BT> BaseTypeDeserializer<BT> baseTypeDeserializer(NamedTypes<BT> namedTypes, Class<BT> baseType) {
            return new BaseTypeDeserializer<>(
                    baseType,
                    "type",
                    namedTypes.all());
        }

    }

    @Configuration
    public static class JobSpecConfiguration {

        @Autowired
        private WeaveProperties weaveProperties;

        @Autowired
        private ObjectMapper jobSpecAwareObjectMapper;

        @Bean
        public JobSpec jobSpec() throws Exception {
            LOGGER.info("Parse JobSpec from file [" + weaveProperties.getJobSpec() + "]...");
            JobSpec jobSpec = jobSpecAwareObjectMapper.readValue(new File(weaveProperties.getJobSpec()), JobSpec.class);
            LOGGER.info("Parse JobSpec from file [" + weaveProperties.getJobSpec() + "]...DONE!");
            return jobSpec;
        }

    }

    @Configuration
    public static class JobWorkerTypeConfiguration {

        @Autowired
        private AppConfig appConfig;

        @Autowired
        private JobSpec jobSpec;

        @Bean
        public JobWorker jobWorker() throws Exception {
            String type = appConfig.getJobWorkerType();
            Class<?> clazz = Class.forName(type);

            if (!JobWorker.class.isAssignableFrom(clazz)) {
                throw new Exception("AppConfig.jobWorkerType is not a class extends from " + JobWorker.class.getName());
            }

            if (Modifier.isAbstract(clazz.getModifiers())) {
                throw new Exception("AppConfig.jobWorkerType is an abstract class");
            }

            // TODO: job worker might need to initialize some resources or get some dependency resources
            Class<? extends JobWorker> cls = (Class<? extends JobWorker>)clazz;
            return cls.getDeclaredConstructor(ManagedResources.class, jobSpec.getClass()).newInstance(jobSpec);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class JobConfiguration {

        @Autowired
        private WeaveProperties weaveProperties;

        @Autowired
        private JobSpec jobSpec;

        @Autowired
        private JobWorker jobWorker;

        @Autowired
        private ActorBindings<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterBindings;

        @Autowired
        private ActorBindings<FileHandlingControl, FileHandler<?, FileHandlingControl>, Object> fileHandlerBindings;

        @Autowired
        private DataClientRegistry dataClientRegistry;

        @Autowired
        private JobBuilderFactory jobBuilders;

        @Autowired
        private StepBuilderFactory stepBuilders;

        @Bean
        public Job job() {
            LOGGER.info("Wire Weave Batch job execution runtime...");

            String jobName = jobSpec.getId() + "-" + System.nanoTime();

            ManagedResources managedResources = new ManagedResourcesImpl();
            managedResources.setResource(Constants.JOB_NAME, jobName);
            managedResources.setResource(Constants.WORK_DIR, weaveProperties.getWorkDir());
            managedResources.setResource(Constants.JOB_SPEC, jobSpec);
            managedResources.setResource(Constants.JOB_WORKER, jobWorker);

            managedResources.setResource(ResourceTypes.DATA_CLIENT_REGISTRY, dataClientRegistry);

            FileHandlerFactory fileHandlerFactory = new FileHandlerFactory(
                    dataClientRegistry,
                    fileHandlerBindings,
                    fileFilterBindings
            );
            managedResources.setResource(ResourceTypes.FILE_HANDLER_FACTORY, fileHandlerFactory);

            Job job = jobBuilders.get(jobName)
                    .start(setup(managedResources))
                    .next(process(managedResources))
                    .next(teardown(managedResources))
                    .listener(new WeaveJobExcutionListener())
                    .validator(new ExitCodeJobParameterValidator())
                    .build();

            LOGGER.info("Wire Weave Batch job execution runtime...DONE!");
            return job;
        }

        public Step setup(ManagedResources managedResources) {
            return stepBuilders.get("weave-batch-setup")
                    .tasklet(new SetupTasklet(managedResources))
                    .build();
        }

        public Step process(ManagedResources managedResources) {
            return stepBuilders.get("weave-batch-process")
                    .tasklet(new ProcessTasklet(managedResources))
                    .build();
        }

        public Step teardown(ManagedResources managedResources) {
            return stepBuilders.get("weave-batch-teardown")
                    .tasklet(new TeardownTasklet(managedResources))
                    .build();
        }

    }

}
