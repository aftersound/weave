package io.aftersound.weave.batch.jobspec;

import java.util.Collection;

public interface JobSpecRegistry {
    JobSpec getJobSpec(String id);
    Collection<JobSpec> all();
}
