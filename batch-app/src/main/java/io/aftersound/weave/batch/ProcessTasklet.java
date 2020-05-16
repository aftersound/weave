package io.aftersound.weave.batch;

import io.aftersound.weave.batch.worker.JobWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class ProcessTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTasklet.class);

    private final JobWorker jobWorker;

    public ProcessTasklet(JobWorker jobWorker) {
        this.jobWorker = jobWorker;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LOGGER.info("Process");

        jobWorker.execute();

        return RepeatStatus.FINISHED;
    }

}
