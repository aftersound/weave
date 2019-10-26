package io.aftersound.weave.batch;

import io.aftersound.weave.batch.fs.FileSystemDataSourceControl;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.filehandler.FileListBasedFileFilterControl;
import io.aftersound.weave.fs.FSFileHandlingControl;
import io.aftersound.weave.resources.ManagedResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

import static io.aftersound.weave.common.Keys.RETURN_INFO;
import static io.aftersound.weave.common.Keys.RETURN_INFOS;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.FILE_LIST;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.TARGET_FILE_PATH;

class FileSourceTasklet implements Tasklet {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSourceTasklet.class);

    private final ManagedResources managedResouces;

    FileSourceTasklet(ManagedResources managedResources) {
        this.managedResouces = managedResources;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        FileHandlingControl fhc = getSourceFileHandlingControl();
        if (fhc == null) {
            return RepeatStatus.FINISHED;
        }

        if (FileSystemDataSourceControl.TYPE.name().equals(fhc.getType())) {
            setJobSpecAsWorkerJobSpec();
        } else {
            FileHandler fileHandler = getFileHandler(fhc);
            LOGGER.info("Using fileHandler: " + fileHandler);

            Result result = fileHandler.listFiles();
            if (result.isFailure()) {
                throw new Exception(result.getFailureReason());
            }

            List<String> filePaths = (List<String>)(result.get(RETURN_INFO).get(FILE_LIST));

            //   Set up local directory that holds data files to be copied from data source
            String localDataDir = managedResouces.getResource(ResourceTypes.JOB_DATA_DIR);
            Result filesCopyResult = fileHandler.copyFilesFrom(filePaths, localDataDir);
            if (filesCopyResult.isFailure()) {
                throw new Exception(result.getFailureReason());
            }

            if (filesCopyResult.isPartialFailure()) {
                throw new Exception(result.getMessage());
            }

            List<String> localFilePaths = filesCopyResult.get(RETURN_INFOS).extractValues(TARGET_FILE_PATH);

            // 3.Once copy is done, create LocalFSDataSourceControl for worker Tasklet
            createAndSetWorkerJobSpec(localDataDir, localFilePaths);
        }

        return RepeatStatus.FINISHED;
    }

    private FileHandlingControl getSourceFileHandlingControl() {
        JobSpec jobSpec = managedResouces.getResource(Constants.JOB_SPEC);

        if (!WithSourceFileHandlingControl.class.isInstance(jobSpec)) {
            return null;
        }

        return ((WithSourceFileHandlingControl)jobSpec).getSourceFileHandlingControl();
    }

    private FileHandler getFileHandler(FileHandlingControl fhc) throws Exception {
        FileHandlerFactory fileHandlerFactory = managedResouces.getResource(ResourceTypes.FILE_HANDLER_FACTORY);
        return fileHandlerFactory.getFileHandler(fhc);
    }

    private void setJobSpecAsWorkerJobSpec() {
        JobSpec jobSpec = managedResouces.getResource(Constants.JOB_SPEC);
        managedResouces.setResource(Constants.WORKER_JOB_SPEC, jobSpec);
    }

    private void createAndSetWorkerJobSpec(String directory, List<String> localFilePaths) {
        JobSpec jobSpec = managedResouces.getResource(Constants.JOB_SPEC);

        JobSpec workerJobSpec = jobSpec.copy();

        workerJobSpec.setId(jobSpec.getId());

        FSFileHandlingControl fhc = new FSFileHandlingControl();
        fhc.setDirectory(directory);
        FileListBasedFileFilterControl filterControl = new FileListBasedFileFilterControl();
        filterControl.setFilePaths(localFilePaths);
        fhc.setFileFilterControl(filterControl);

        ((WithSourceFileHandlingControl)workerJobSpec).setSourceFileHandlingControl(fhc);

        managedResouces.setResource(Constants.WORKER_JOB_SPEC, workerJobSpec);
    }

}
