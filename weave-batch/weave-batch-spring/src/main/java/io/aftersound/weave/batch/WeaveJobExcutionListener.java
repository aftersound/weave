package io.aftersound.weave.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class WeaveJobExcutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Set expected exit code when a job is about to finish
//        jobExecution.setExitStatus(ExitStatus.COMPLETED);
    }
}
