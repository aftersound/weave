package io.aftersound.weave.batch;

import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.ManagedComponents;
import io.aftersound.weave.utils.PathHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

class SetupTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupTasklet.class);

    private final ManagedComponents managedComponents;

    SetupTasklet(ManagedComponents managedComponents) {
        this.managedComponents = managedComponents;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Set up");

        setupWorkDirectory();
        setupDataClientRegistry();

        return RepeatStatus.FINISHED;
    }

    private void setupWorkDirectory() throws Exception {
        JobSpec jobSpec = managedComponents.getComponent(Constants.JOB_SPEC);
        String jobName = managedComponents.getComponent(Constants.JOB_NAME);

        Path workDir = PathHandle.of(managedComponents.getComponent(Constants.WORK_DIR)).path();

        Path jobDataDir = Paths.get(workDir.toString(), jobSpec.getId(), "data");
        Files.createDirectories(jobDataDir);
        managedComponents.setComponent(Constants.JOB_DATA_DIR.name(), jobDataDir.toString());

        Path jobLogDir = Paths.get(workDir.toString(), jobSpec.getId(), "log", jobName);
        Files.createDirectories(jobLogDir);
        managedComponents.setComponent(Constants.JOB_LOG_DIR.name(), jobDataDir.toString());
    }

    private void setupDataClientRegistry() throws Exception {
        LOGGER.info("Initializing data clients based on JobSpec.dataSourceControls");

        JobSpec jobSpec = managedComponents.getComponent(Constants.JOB_SPEC);

        if (!(jobSpec instanceof DataSourceAwareJobSpec)) {
            return;
        }

        List<DataSourceControl> dscs = ((DataSourceAwareJobSpec)jobSpec).getDataSourceControls();
        if (dscs == null || dscs.isEmpty()) {
            return;
        }

        ComponentRegistry cr = managedComponents.getComponent(ResourceTypes.COMPONENT_REGISTRY);
        for (DataSourceControl dsc : dscs) {
            cr.initializeComponent(ComponentConfig.of(dsc.getType(), dsc.getId(), dsc.getOptions()));
        }
    }

}
