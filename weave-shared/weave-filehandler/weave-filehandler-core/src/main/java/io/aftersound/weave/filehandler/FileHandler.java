package io.aftersound.weave.filehandler;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.file.PathHandle;

import java.util.List;

/**
 * File handler which acts in according to given {@link FileHandlingControl}
 * Concrete implementation must have static field COMPANION_CONTROL_TYPE to
 * declare its companion {@link FileHandlingControl} type.
 */
public abstract class FileHandler<CLIENT, CONTROL extends FileHandlingControl> {

    /**
     * DataClientRegistry which provides access to client connecting to data repository
     */
    protected final DataClientRegistry dataClientRegistry;

    /**
     * FileFilterFactory which provides access to FileFilter(s)
     */
    protected final ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory;

    /**
     * FileHandleControl contains information such as data repository, filter control
     */
    protected final CONTROL control;

    // Force implementation to follow same constructor signature
    protected FileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            CONTROL control) {
        this.dataClientRegistry = dataClientRegistry;
        this.fileFilterFactory = fileFilterFactory;
        this.control = control;
    }

    /**
     * Get short file name.
     * More of a helper and expect to be overridden for customized behavior.
     *
     * @param filePath
     *          - complete file path
     * @return short file name
     */
    public String getShortFileName(String filePath) {
        return PathHandle.of(filePath).path().getFileName().toString();
    }

    /**
     * Get list of files from data repository
     * @return Result indicator and list of files
     */
    public abstract Result listFiles();

    /**
     * Copy file from data source to local file system
     * @param sourceFilePath
     *          - source file path on data source
     * @param targetFilePath
     *          - target file path on local file system
     * @return Result indicator of copy success or failure
     */
    public abstract Result copyFileFrom(String sourceFilePath, String targetFilePath);

    /**
     * Copy files from data source to local file system
     * @param sourceFilePaths
     * @param targetDirectory
     * @return Result indicator of copy success or failure
     */
    public abstract Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory);

    /**
     * Delete file on data repository specified in given FileHandleControl
     * @param filePath
     * @return Result indicator of deletion success or failure
     */
    public abstract Result deleteFile(String filePath);

    /**
     * Copy file from local file system to data source
     * @param sourceFilePath
     *          - source file path on local file system
     * @param targetFilePath
     *          - target file path on data source
     * @return Result indicator of copy failure or success
     */
    public abstract Result copyLocalFileTo(String sourceFilePath, String targetFilePath);

    /**
     * Copy file from local file system to data source,
     * detailed location on target file storage is expected
     * to be specified in CONTROL
     * @param sourceFilePath
     *          - source file path on local file system
     * @return Result indicator of copy failure or success
     */
    public abstract Result copyLocalFileTo(String sourceFilePath);

    /**
     * Copy files from local file system to data source
     * @param sourceFilePaths
     *          - source file paths on local file system
     * @param targetDirectory
     *          - target directory on data source
     * @return Result indicator of copy success or failure
     */
    public abstract Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory);

    /**
     * Copy files from local file system to data source,
     * detailed location on target file storage is expected
     * to be specified in CONTROL
     * @param sourceFilePaths
     *          - source file paths on local file system
     * @return Result indicator of copy success or failure
     */
    public abstract Result copyLocalFilesTo(List<String> sourceFilePaths);

}
