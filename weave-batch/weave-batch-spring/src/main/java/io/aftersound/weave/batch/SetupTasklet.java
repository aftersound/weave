package io.aftersound.weave.batch;

import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.file.PathHandle;
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

    private final ManagedResources managedResources;

    SetupTasklet(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Set up");

        setupWorkDirectory();
        setupDataClientRegistry();

        return RepeatStatus.FINISHED;
    }

    private void setupWorkDirectory() throws Exception {
        JobSpec jobSpec = managedResources.get(Constants.MR_JOB_SPEC);
        String jobName = managedResources.get(Constants.MR_JOB_NAME);

        Path workDir = PathHandle.of(managedResources.get(Constants.MR_WORK_DIR)).path();

        Path jobDataDir = Paths.get(workDir.toString(), jobSpec.getId(), "data");
        Files.createDirectories(jobDataDir);
        managedResources.set(Constants.MR_JOB_DATA_DIR, jobDataDir.toString());

        Path jobLogDir = Paths.get(workDir.toString(), jobSpec.getId(), "log", jobName);
        Files.createDirectories(jobLogDir);
        managedResources.set(Constants.MR_JOB_LOG_DIR, jobDataDir.toString());
    }

    private void setupDataClientRegistry() throws Exception {
        LOGGER.info("Initializing data clients based on JobSpec.dataSourceControls");

        JobSpec jobSpec = managedResources.get(Constants.MR_JOB_SPEC);

        if (!(jobSpec instanceof DataSourceAwareJobSpec)) {
            return;
        }

        List<DataSourceControl> dscs = ((DataSourceAwareJobSpec)jobSpec).getDataSourceControls();
        if (dscs == null || dscs.isEmpty()) {
            return;
        }

        DataClientRegistry dcr = managedResources.get(Constants.MR_DATA_CLIENT_REGISTRY);
        for (DataSourceControl dsc : dscs) {
            dcr.initializeClient(dsc.getType(), dsc.getId(), dsc.getOptions());
        }

    }

}
