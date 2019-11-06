package io.aftersound.weave.batch;

import io.aftersound.weave.resources.ManagedResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class TeardownTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeardownTasklet.class);

    private final ManagedResources managedResources;

    public TeardownTasklet(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Tear down");

        return RepeatStatus.FINISHED;
    }

}
