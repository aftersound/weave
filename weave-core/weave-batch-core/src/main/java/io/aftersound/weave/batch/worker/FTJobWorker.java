package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.ResourceTypes;
import io.aftersound.weave.batch.jobspec.ft.FTJobSpec;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.config.Config;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.resources.ResourceInitializer;
import io.aftersound.weave.resources.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.aftersound.weave.common.Keys.RETURN_INFO;
import static io.aftersound.weave.common.Keys.RETURN_INFOS;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.FILE_LIST;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.TARGET_FILE_PATH;

public class FTJobWorker extends JobWorker<FTJobSpec> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTJobWorker.class);

    public static final ResourceInitializer RESOURCE_INITIALIZER = new ResourceInitializer() {

        @Override
        public ResourceType<?>[] getDependingResourceTypes() {
            return new ResourceType[] {
                    ResourceTypes.FILE_HANDLER_FACTORY,
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
        FileHandlerFactory fileHandlerFactory = managedResources.getResource(ResourceTypes.FILE_HANDLER_FACTORY);
        //   Set up local directory that holds data files to be copied from data source
        String localDataDir = managedResources.getResource(ResourceTypes.JOB_DATA_DIR);

        // 1.copy data files from source file storage to local
        FileHandlingControl sourceControl = jobSpec.getSourceFileHandlingControl();
        FileHandler sourceFileHandler = fileHandlerFactory.getFileHandler(sourceControl);
        LOGGER.info("Using fileHandler: " + sourceFileHandler);

        Result result = sourceFileHandler.listFiles();
        if (result.isFailure()) {
            throw new Exception(result.getFailureReason());
        }

        List<String> filePaths = (List<String>)(result.get(RETURN_INFO).get(FILE_LIST));

        Result filesCopyResult = sourceFileHandler.copyFilesFrom(filePaths, localDataDir);
        if (filesCopyResult.isFailure()) {
            throw new Exception(result.getFailureReason());
        }

        if (filesCopyResult.isPartialFailure()) {
            throw new Exception(result.getMessage());
        }

        List<String> localFilePaths = filesCopyResult.get(RETURN_INFOS).extractValues(TARGET_FILE_PATH);

        // 2.copy local data files to target file storage
        FileHandlingControl targetControl = jobSpec.getTargetFileHandlingControl();
        FileHandler targetFileHandler = fileHandlerFactory.getFileHandler(targetControl);
        targetFileHandler.copyLocalFilesTo(localFilePaths);
    }
}
