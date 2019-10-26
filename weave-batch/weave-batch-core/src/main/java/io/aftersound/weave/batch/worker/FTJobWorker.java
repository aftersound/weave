package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.ResourceTypes;
import io.aftersound.weave.batch.jobspec.ft.FTJobSpec;
import io.aftersound.weave.config.Config;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.resources.ResourceInitializer;
import io.aftersound.weave.resources.ResourceType;

public class FTJobWorker extends JobWorker<FTJobSpec> {

    public static final ResourceInitializer RESOURCE_INITIALIZER = new ResourceInitializer() {

        @Override
        public ResourceType<?>[] getDependingResourceTypes() {
            return new ResourceType[] {
                    ResourceTypes.DATA_CLIENT_REGISTRY,
                    ResourceTypes.JOB_DATA_DIR
            };
        }

        @Override
        public ResourceType<?>[] getShareableResourceTypes() {
            return new ResourceType[0];
        }

        @Override
        public ResourceType<?>[] getResourceTypes() {
            return new ResourceType[0];
        }

        @Override
        public void initializeResources(ManagedResources managedResources, Config resourceConfig) throws Exception {
        }
    };

    public FTJobWorker(ManagedResources managedResources, FTJobSpec jobSpec) {
        super(managedResources, jobSpec);
    }

    @Override
    public void execute() throws Exception {
        DataClientRegistry dcr = managedResources.getResource(ResourceTypes.DATA_CLIENT_REGISTRY);

        // TODO: how to get hold of data client for source file handling?
        // TODO: how to get hold of FileHandler for source file handler?
        jobSpec.getSourceFileHandlingControl();

        // TODO: how to get hold of data client for target file handling?
        // TODO: how to get hold of FileHandler for target file handler?
        jobSpec.getTargetFileHandlingControl();
    }
}
