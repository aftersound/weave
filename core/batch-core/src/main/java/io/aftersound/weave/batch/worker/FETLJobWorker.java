package io.aftersound.weave.batch.worker;

import io.aftersound.weave.batch.ResourceTypes;
import io.aftersound.weave.batch.jobspec.fetl.FETLJobSpec;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.resource.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static io.aftersound.weave.common.Result.RETURN_INFO;
import static io.aftersound.weave.common.Result.RETURN_INFOS;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.FILE_LIST;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.TARGET_FILE_PATH;

public class FETLJobWorker extends JobWorker<FETLJobSpec> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FETLJobWorker.class);

    public static final ResourceDeclaration RESOURCE_DECLARATION = SimpleResourceDeclaration.withRequired(
            ResourceTypes.FILE_HANDLER_FACTORY,
            ResourceTypes.JOB_DATA_DIR
    );

    public FETLJobWorker(ManagedResources managedResources, FETLJobSpec jobSpec) {
        super(managedResources, jobSpec);
    }

    @Override
    public void execute() throws Exception {
        FileHandlerFactory fileHandlerFactory = managedResources.getResource(ResourceTypes.FILE_HANDLER_FACTORY);
        //   Set up local directory that holds data files to be copied from data source
        String localDataDir = managedResources.getResource(ResourceTypes.JOB_DATA_DIR);

        FileHandlingControl sourceFileHandlingControl = jobSpec.getSourceFileHandlingControl();
        FileHandler sourceFileHandler = fileHandlerFactory.getFileHandler(sourceFileHandlingControl);
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

        // ETL with data file available at local
        // TODO
    }
}
