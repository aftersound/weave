package io.aftersound.weave.batch;

class Constants {

    // Namespaces
    static final String NS_JOB_SPEC = "job-spec";

    // Keys of Job Parameters
    static final String JP_JOB_SPEC = "--jobSpec";

    // Keys of Managed Resources
    static final String MR_JOB_NAME = "JOB_NAME";
    static final String MR_WORK_DIR = "WORK_DIR";
    static final String MR_JOB_DATA_DIR = "JOB_DATA_DIR";
    static final String MR_JOB_LOG_DIR = "JOB_LOG_DIR";
    static final String MR_JOB_SPEC = "JOB_SPEC";
    static final String MR_DATA_CLIENT_REGISTRY = "DATA_CLIENT_REGISTRY";
    static final String MR_FILE_HANDLER_FACTORY = "FILE_HANDLER_FACTORY";
    static final String MR_WORKER_JOB_SPEC = "WORKER_JOB_SPEC";

    // Keys of System Properties
    static final String SP_JOB_NAME = "jobName";

    // Default values
    static final String DEFAULT_WORK_DIR = "${java.io.tmpdir}/weave";
}
