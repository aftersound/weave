package io.aftersound.weave.batch;

import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.worker.JobWorker;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.resources.ResourceType;

class Constants {

    // Namespaces
    static final String NS_JOB_SPEC = "job-spec";

    // Keys of Job Parameters
    static final String JP_JOB_SPEC = "--jobSpec";

    // Keys of Managed Resources
    static final ResourceType<String> JOB_NAME = new ResourceType<>("JOB_NAME", String.class);
    static final ResourceType<JobSpec> JOB_SPEC = new ResourceType<>("JOB_SPEC", JobSpec.class);
    static final ResourceType<String> WORK_DIR = new ResourceType<>("WORK_DIR", String.class);
    static final ResourceType<String> JOB_DATA_DIR = new ResourceType<>("JOB_DATA_DIR", String.class);
    static final ResourceType<String> JOB_LOG_DIR = new ResourceType<>("JOB_LOG_DIR", String.class);
    static final ResourceType<JobWorker> JOB_WORKER = new ResourceType<>("JOB_WORKER", JobWorker.class);
    static final ResourceType<JobSpec> WORKER_JOB_SPEC = new ResourceType<>("WORKER_JOB_SPEC", JobSpec.class);


    static final String MR_JOB_SPEC = "JOB_SPEC";
    static final String MR_WORKER_JOB_SPEC = "WORKER_JOB_SPEC";

    // Keys of System Properties
    static final String SP_JOB_NAME = "jobName";

    // Default values
    static final String DEFAULT_WORK_DIR = "${java.io.tmpdir}/weave";
}
