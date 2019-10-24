package io.aftersound.weave.batch.spark;

import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.common.NamedType;

public class SparkSubmissionJobSpec extends DataSourceAwareJobSpec {

    public static final NamedType<JobSpec> TYPE = NamedType.of("SPARK_JOB_SUBMISSION", SparkSubmissionJobSpec.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    @Override
    public JobSpec copy() {
        SparkSubmissionJobSpec c = new SparkSubmissionJobSpec();
        // TODO:
        return c;
    }
}
