package io.aftersound.weave.swift;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.filehandler.FileFilter;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlingControl;
import org.javaswift.joss.model.Account;

import java.util.List;

public class SwiftFileHandler extends FileHandler<Account, SwiftFileHandlingControl> {

    public static final NamedType<FileHandlingControl> COMPANION_CONTROL_TYPE = SwiftFileHandlingControl.TYPE;

    private final SwiftAccountDelegate delegate;

    public SwiftFileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            SwiftFileHandlingControl control) {
        super(dataClientRegistry, fileFilterFactory, control);
        Account account = dataClientRegistry.getClient(control.getType());
        this.delegate = new SwiftAccountDelegate(account);
    }

    @Override
    public Result listFiles() {
        // TODO: filtering based on SwiftFileHandlingControl
        return delegate.listFiles(control.getContainerName());
    }

    @Override
    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        // TODO: overwrite handling
        return delegate.copyFileFrom(control.getContainerName(), sourceFilePath, targetFilePath, true);
    }

    @Override
    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        // TODO: overwrite handling
        return delegate.copyFilesFrom(control.getContainerName(), sourceFilePaths.toArray(new String[0]), targetDirectory, true);
    }

    @Override
    public Result deleteFile(String filePath) {
        return delegate.deleteFile(control.getContainerName(), filePath);
    }

    @Override
    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        // TODO: TTL and overwrite handling
        return delegate.copyLocalFileTo(control.getContainerName(), sourceFilePath, targetFilePath, true, null);
    }

    @Override
    public Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory) {
        // TODO: TTL and overwrite handling
        return delegate.copyLocalFilesTo(control.getContainerName(), sourceFilePaths.toArray(new String[0]), targetDirectory, true, null);
    }
}
