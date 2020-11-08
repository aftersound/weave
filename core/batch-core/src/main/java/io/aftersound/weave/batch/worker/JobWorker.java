package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.component.ManagedComponents;

public abstract class JobWorker<SPEC extends JobSpec> {

    protected final ManagedComponents managedComponents;
    protected final SPEC jobSpec;

    public JobWorker(ManagedComponents managedComponents, SPEC jobSpec) {
        this.managedComponents = managedComponents;
        this.jobSpec = jobSpec;
    }

    public abstract void execute() throws Exception;
}
