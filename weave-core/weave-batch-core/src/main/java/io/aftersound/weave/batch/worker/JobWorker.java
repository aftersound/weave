package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.resources.ManagedResources;

public abstract class JobWorker<SPEC extends JobSpec> {

    protected final ManagedResources managedResources;
    protected final SPEC jobSpec;

    public JobWorker(ManagedResources managedResources, SPEC jobSpec) {
        this.managedResources = managedResources;
        this.jobSpec = jobSpec;
    }

    public abstract void execute() throws Exception;
}
