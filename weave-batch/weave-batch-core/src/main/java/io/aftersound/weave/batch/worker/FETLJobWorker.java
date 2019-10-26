package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.jobspec.fetl.FETLJobSpec;
import io.aftersound.weave.resources.ManagedResources;

public class FETLJobWorker extends JobWorker<FETLJobSpec> {

    public FETLJobWorker(ManagedResources managedResources, FETLJobSpec jobSpec) {
        super(managedResources, jobSpec);
    }

    @Override
    public void execute() throws Exception {

    }
}
