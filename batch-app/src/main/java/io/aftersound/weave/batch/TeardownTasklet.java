package io.aftersound.weave.batch;

import io.aftersound.weave.component.ManagedComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class TeardownTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeardownTasklet.class);

    private final ManagedComponents managedComponents;

    public TeardownTasklet(ManagedComponents managedComponents) {
        this.managedComponents = managedComponents;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Tear down");

        return RepeatStatus.FINISHED;
    }

}
