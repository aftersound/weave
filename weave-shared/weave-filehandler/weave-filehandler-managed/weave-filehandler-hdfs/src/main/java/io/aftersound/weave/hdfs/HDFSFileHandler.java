package io.aftersound.weave.hdfs;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.filehandler.*;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HDFSFileHandler extends FileHandler<FileSystem, HDFSFileHandlingControl> {

    public static final NamedType<FileHandlingControl> COMPANION_CONTROL_TYPE = HDFSFileHandlingControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(HDFSFileHandler.class);

    private final FileSystemDelegate delegate;

    public HDFSFileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            HDFSFileHandlingControl control) {
        super(dataClientRegistry, fileFilterFactory, control);
        FileSystem fileSystem = dataClientRegistry.getClient(control.getClientId());
        this.delegate = new FileSystemDelegate(fileSystem);
    }

    @Override
    public Result listFiles() {
        try {
            FileFilter fileFilter = fileFilterFactory.createActor(control.getFileFilterControl());
            return listFiles(fileFilter);
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    private Result listFiles(FileFilter fileFilter) {
        return Result.failure("NotImplemented");
    }

    private Result listByFlagFileNamePattern() {
        return Result.failure("NotImplemented");
    }

    private Result listByDataFileNamePattern() {
        return Result.failure("NotImplemented");
    }

    private Result listAll() {
        return Result.failure("NotImplemented");
    }

    @Override
    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        return delegate.copyFileFrom(sourceFilePath, targetFilePath);
    }

    @Override
    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        return delegate.copyFilesFrom(sourceFilePaths, targetDirectory);
    }

    @Override
    public Result deleteFile(String filePath) {
        return delegate.deleteFile(filePath);
    }

    @Override
    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        return delegate.copyLocalFileTo(sourceFilePath, targetFilePath);
    }

    @Override
    public Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory) {
        return delegate.copyLocalFilesTo(sourceFilePaths, targetDirectory);
    }
}
