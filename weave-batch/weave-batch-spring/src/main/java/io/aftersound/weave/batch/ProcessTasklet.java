package io.aftersound.weave.batch;

import io.aftersound.weave.batch.worker.JobWorker;
import io.aftersound.weave.resources.ManagedResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ProcessTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTasklet.class);

    private final ManagedResources managedResources;

    public ProcessTasklet(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Process");

        JobWorker jobWorker = managedResources.getResource(Constants.JOB_WORKER);
        jobWorker.execute();

        return RepeatStatus.FINISHED;
    }

}
