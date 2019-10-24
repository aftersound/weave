package io.aftersound.weave.knox;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.filehandler.FileFilter;
import io.aftersound.weave.filehandler.FileFilterControl;
import io.aftersound.weave.filehandler.FileHandler;
import io.aftersound.weave.filehandler.FileHandlingControl;
import org.apache.knox.gateway.shell.KnoxSession;

import java.util.List;

public class KnoxFileHandler extends FileHandler<KnoxSession, KnoxFileHandlingControl> {

    public static final NamedType<FileHandlingControl> COMPANION_CONTROL_TYPE = KnoxFileHandlingControl.TYPE;

    private final KnoxSessionDelegate delegate;

    public KnoxFileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            KnoxFileHandlingControl control) {
        super(dataClientRegistry, fileFilterFactory, control);
        KnoxSession session = dataClientRegistry.getClient(control.getType());
        this.delegate = new KnoxSessionDelegate(session);
    }

    @Override
    public Result listFiles() {
        // TODO: KnoxFileHandlingControl
        return delegate.listFiles("/user/guest/example");
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
        return delegate.copyLocalFilesTo(sourceFilePaths.toArray(new String[0]), targetDirectory);
    }
}
