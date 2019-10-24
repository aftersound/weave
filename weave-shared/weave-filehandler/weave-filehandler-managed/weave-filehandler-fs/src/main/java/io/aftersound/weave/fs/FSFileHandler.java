package io.aftersound.weave.fs;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.common.ReturnInfo;
import io.aftersound.weave.common.ReturnInfos;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.filehandler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static io.aftersound.weave.common.Keys.RETURN_INFO;
import static io.aftersound.weave.common.Keys.RETURN_INFOS;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.*;

public class FSFileHandler extends FileHandler<Void, FSFileHandlingControl> {

    public static final NamedType<FileHandlingControl> COMPANION_CONTROL_TYPE = FSFileHandlingControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(FSFileHandler.class);

    public FSFileHandler(
            DataClientRegistry dataClientRegistry,
            ActorFactory<FileFilterControl, FileFilter<FileFilterControl>, Object> fileFilterFactory,
            FSFileHandlingControl control) {
        super(dataClientRegistry, fileFilterFactory, control);
    }

    @Override
    public Result listFiles() {
        Path dir = PathHandle.of(control.getDirectory()).path();

        try {
            final FileFilter fileFilter = fileFilterFactory.createActor(control.getFileFilterControl());

            DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
                public boolean accept(Path file) throws IOException {
                    return fileFilter.accept(file.toString());
                }
            };

            List<String> found = new ArrayList<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
                for (Path path : stream) {
                    found.add(path.toString());
                }
            }

            return Result.success().set(RETURN_INFO, new ReturnInfo().set(FILE_LIST, found));
        } catch (Exception e) {
            return Result.failure("failed to list files under directory [" + dir.toString() + "]", e);
        }
    }

    @Override
    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        Path sourcePath = PathHandle.of(sourceFilePath).path();
        Path targetPath = PathHandle.of(targetFilePath).path();
        ReturnInfo ri = new ReturnInfo()
                .set(SOURCE_FILE_PATH, sourcePath.toString())
                .set(TARGET_FILE_PATH, targetPath.toString());
        return copyFile(sourcePath, targetPath).set(RETURN_INFO, ri);
    }

    @Override
    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        Result result = copyFiles(sourceFilePaths, targetDirectory);
        return result.set(RETURN_INFOS, result.get(RETURN_INFOS));
    }

    @Override
    public Result deleteFile(String filePath) {
        Path path = PathHandle.of(filePath).path();
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (Exception e) {
                LOGGER.error("failed to delete file at " + path.toString(), e);
                return Result.failure("failed to delete file at " + path.toString(), e);
            }
        }
        return Result.success();
    }

    @Override
    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        Path sourcePath = PathHandle.of(sourceFilePath).path();
        Path targetPath = PathHandle.of(targetFilePath).path();
        ReturnInfo ri = new ReturnInfo()
                .set(SOURCE_FILE_PATH, sourcePath.toString())
                .set(TARGET_FILE_PATH, targetPath.toString());
        return copyFile(sourcePath, targetPath).set(RETURN_INFO, ri);
    }

    @Override
    public Result copyLocalFilesTo(List<String> sourceFilePaths, String targetDirectory) {
        Result result = copyFiles(sourceFilePaths, targetDirectory);
        return result.set(RETURN_INFOS, result.get(RETURN_INFOS));
    }

    private Result copyFile(Path sourcePath, Path targetPath) {
        try {
            Files.createDirectories(targetPath.getParent());
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            return Result.success();
        } catch (Exception e) {
            return Result.failure("failed to copy file at " + sourcePath.toString() + " to " + targetPath.toString(), e);
        }
    }

    private Result copyFiles(List<String> sourceFilePaths, String targetDirectory) {
        Result compositeResult = Result.composite(sourceFilePaths.size());
        compositeResult.set(RETURN_INFOS, new ReturnInfos());
        for (String sourceFilePath : sourceFilePaths) {
            String targetFilePath = figureOutTargetFilePath(sourceFilePath, targetDirectory);
            Path sourcePath = Paths.get(sourceFilePath);
            Path targetPath = Paths.get(targetFilePath);
            Result result = copyFile(sourcePath, targetPath);

            ReturnInfo ri = new ReturnInfo()
                    .set(SOURCE_FILE_PATH, sourcePath.toString())
                    .set(TARGET_FILE_PATH, targetPath.toString());
            compositeResult.addSubResult(result.set(RETURN_INFO, ri));
            if (result.isSuccess()) {
                compositeResult.get(RETURN_INFOS).addSucceeded(ri);
            } else {
                compositeResult.get(RETURN_INFOS).addFailed(ri);
            }
        }
        return compositeResult;
    }

    private String figureOutTargetFilePath(String sourceFile, String targetDirectory) {
        String shortFileName = Paths.get(sourceFile).getFileName().toString();
        return Paths.get(targetDirectory, shortFileName).toString();
    }

}
