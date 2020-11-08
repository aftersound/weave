package io.aftersound.weave.batch;

import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.common.NamedType;

class Constants {
    // Keys of Managed Resources
    static final NamedType<String> JOB_NAME = NamedType.of("JOB_NAME", String.class);
    static final NamedType<JobSpec> JOB_SPEC = NamedType.of("JOB_SPEC", JobSpec.class);
    static final NamedType<String> WORK_DIR = NamedType.of("WORK_DIR", String.class);
    static final NamedType<String> JOB_DATA_DIR = NamedType.of("JOB_DATA_DIR", String.class);
    static final NamedType<String> JOB_LOG_DIR = NamedType.of("JOB_LOG_DIR", String.class);
}
