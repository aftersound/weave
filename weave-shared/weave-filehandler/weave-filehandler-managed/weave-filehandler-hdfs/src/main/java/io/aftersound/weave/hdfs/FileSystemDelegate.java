package io.aftersound.weave.hdfs;

import io.aftersound.weave.common.Result;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class FileSystemDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemDelegate.class);

    private final FileSystem fileSystem;

    public FileSystemDelegate(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        try {
            fileSystem.copyToLocalFile(new Path(sourceFilePath), new Path(targetFilePath));
            LOGGER.info("copyFileFrom(" + sourceFilePath + ", " + targetFilePath + ") succeeds");
            return Result.success();
        } catch (IOException e) {
            LOGGER.error("copyFileFrom(" + sourceFilePath + ", " + targetFilePath + ") failed due to", e);
            return Result.failure("copyFileFrom(" + sourceFilePath + ", " + targetFilePath + ") failed", e);
        }
    }

    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        return Result.failure("NotImplemented");
    }

    public Result deleteFile(String filePath) {
        return Result.failure("NotImplemented");
    }

    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        return Result.failure("NotImplemented");
    }

    public Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory) {
        return Result.failure("NotImplemented");
    }
}
