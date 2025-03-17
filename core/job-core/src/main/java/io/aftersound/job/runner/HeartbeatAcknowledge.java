package io.aftersound.job.runner;

import io.aftersound.job.JobRequest;

import java.util.List;

public class HeartbeatAcknowledge {
    private String status;
    private List<JobRequest> jobs;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<JobRequest> getJobs() {
        return jobs;
    }

    public void setJobs(List<JobRequest> jobs) {
        this.jobs = jobs;
    }

}
