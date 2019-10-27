package io.aftersound.weave.batch;

import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.worker.JobWorker;
import io.aftersound.weave.resources.ResourceType;

class Constants {
    // Keys of Managed Resources
    static final ResourceType<String> JOB_NAME = new ResourceType<>("JOB_NAME", String.class);
    static final ResourceType<JobSpec> JOB_SPEC = new ResourceType<>("JOB_SPEC", JobSpec.class);
    static final ResourceType<String> WORK_DIR = new ResourceType<>("WORK_DIR", String.class);
    static final ResourceType<String> JOB_DATA_DIR = new ResourceType<>("JOB_DATA_DIR", String.class);
    static final ResourceType<String> JOB_LOG_DIR = new ResourceType<>("JOB_LOG_DIR", String.class);
    static final ResourceType<JobWorker> JOB_WORKER = new ResourceType<>("JOB_WORKER", JobWorker.class);
}
