package io.aftersound.weave.job;

import java.util.Map;

public class JobRequest {
    private String jobId;
    private Map<String, String> jobRunner;
    private Map<String, Object> jobSpec;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Map<String, String> getJobRunner() {
        return jobRunner;
    }

    public void setJobRunner(Map<String, String> jobRunner) {
        this.jobRunner = jobRunner;
    }

    public Map<String, Object> getJobSpec() {
        return jobSpec;
    }

    public void setJobSpec(Map<String, Object> jobSpec) {
        this.jobSpec = jobSpec;
    }

}
