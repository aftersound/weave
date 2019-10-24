package io.aftersound.weave.swift;

import io.aftersound.weave.common.Result;
import io.aftersound.weave.common.ReturnInfo;
import io.aftersound.weave.common.ReturnInfos;
import org.javaswift.joss.headers.object.DeleteAfter;
import org.javaswift.joss.instructions.UploadInstructions;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.aftersound.weave.common.Keys.RETURN_INFO;
import static io.aftersound.weave.common.Keys.RETURN_INFOS;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.SOURCE_FILE_PATH;
import static io.aftersound.weave.filehandler.FileReturnInfoKeys.TARGET_FILE_PATH;
import static io.aftersound.weave.swift.SwiftReturnInfoKeys.*;

public class SwiftAccountDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwiftAccountDelegate.class);

    private final Account account;

    public SwiftAccountDelegate(Account account) {
        this.account = account;
    }

    public Result createContainer(String containerName, boolean makePublic) {
        try {
            Container container = account.getContainer(containerName);
            if (container.exists()) {
                return Result.success("container [" + containerName + "] already exists");
            } else {
                LOGGER.info("container [" + containerName + "] creation started");
                container.create();
                LOGGER.info("container [" + containerName + "] creation completed");
            }

            if (makePublic && !container.isPublic()) {
                container.makePublic();
                LOGGER.info("container [" + containerName + "] is made publicly accessible");
            }
            return Result.success().set(RETURN_INFO, extractInfo(container));
        } catch (Exception e) {
            LOGGER.error("container [" + containerName + "] creation failed", e);
            return Result.failure(e).set(RETURN_INFO, new ReturnInfo());
        }
    }

    public Result createContainers(String[] containerNames, boolean makePublic) {
        Result compositeResult = Result.composite(containerNames.length);
        for (String containerName : containerNames) {
            Result result = createContainer(containerName, makePublic);
            compositeResult.addSubResult(result);
        }

        return compositeResult;
    }

    public Result deleteContainer(String containerName) {
        try {
            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                LOGGER.info("container [" + containerName + "] doesn't exists");
                return Result.success("container [" + containerName + "] doesn't exists");
            }

            LOGGER.info("container [" + containerName + "] deletion started");
            container.delete();
            LOGGER.info("container [" + containerName + "] deletion completed");
            return Result.success();
        } catch (Exception e) {
            LOGGER.error("container [" + containerName + "] deletion failed", e);
            return Result.failure("container [" + containerName + "] deletion failed due to " + e.getMessage());
        }
    }

    public Result deleteContainers(String[] containerNames) {
        Result compositeResult = Result.composite(containerNames.length);
        for (String containerName : containerNames) {
            compositeResult.addSubResult(deleteContainer(containerName));
        }
        return compositeResult;
    }

    public Result getContainerInfo(String containerName) {
        try {
            Container container = account.getContainer(containerName);
            if (container.exists()) {
                return Result.success().set(RETURN_INFO, extractInfo(container));
            } else {
                return Result.success("container [" + containerName + "] does not exist");
            }
        } catch (Exception e) {
            return Result.failure("get information of container [" + containerName + "] failed due to " + e.getMessage());
        }
    }

    public Result listContainers() {
        try {
            Collection<Container> containers = account.list();
            ReturnInfos returnInfos = new ReturnInfos();
            if (containers != null) {
                for (Container c : containers) {
                    returnInfos.addSucceeded(extractInfo(c));
                }
            }
            return Result.success().set(RETURN_INFOS, returnInfos);
        } catch (Exception e) {
            LOGGER.error("list container failed", e);
            return Result.failure("list container failed due to " + e.getMessage());
        }
    }

    public Result listFiles(String containerName) {
        try {
            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                LOGGER.error("container [" + containerName + "] doesn't exist");
                return Result.failure("container [" + containerName + "] does not exist");
            }

            Collection<StoredObject> storedObjects = container.list();
            LOGGER.info("Container: " + toString(container) + " has " + storedObjects.size() + (storedObjects.isEmpty() ?
                    " file or directory" : " files and/or directories"));

            ReturnInfos soInfos = new ReturnInfos();
            List<String> filePaths = new ArrayList<>();
            for (StoredObject so : storedObjects) {
                LOGGER.info(toString(so));

                soInfos.addSucceeded(extractInfo(so));
                filePaths.add(so.getPath());
            }

            ReturnInfo ri = new ReturnInfo()
                    .set(FILE_LIST, filePaths)
                    .set(STORAGE_OBJECTS, soInfos);

            return Result.success().set(RETURN_INFO, ri);
        } catch (Exception e) {
            LOGGER.error("failed to list file/directory in container [" + containerName + "]", e);
            return Result.failure("failed to list file/directory in container [" + containerName + "] due to " + e.getMessage());
        }
    }

    public Result getFileInfo(String containerName, String file) {
        try {
            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                LOGGER.error("container [" + containerName + "] doesn't exist");
                return Result.failure("container [" + containerName + "] does not exist");
            }

            StoredObject so = container.getObject(file);
            if (so.exists()) {
                return Result.success().set(RETURN_INFO, extractInfo(so));
            } else {
                LOGGER.info("file [" + file + "] doesn't exist");
                return Result.success("file [" + file + "] doesn't exist");
            }
        } catch (Exception e) {
            LOGGER.error("failed to get information of file [" + file + "]", e);
            return Result.failure("failed to get information of file [" + file + "] due to " + e.getMessage());
        }
    }

    public Result copyFileFrom(String containerName, String sourceFile, String targetFile, boolean overwrite) {
        try {
            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                LOGGER.error("container [" + containerName + "] doesn't exist");
                return Result.failure("container [" + containerName + "] doesn't exist");
            }

            StoredObject so = container.getObject(sourceFile);
            if (so.exists()) {
                if (Files.exists(Paths.get(targetFile))) {
                    LOGGER.info("file [" + targetFile + "] already exists");
                    if (!overwrite) {
                        return Result.failure("file [" + targetFile + "] already exists in specified container");
                    } else {
                        LOGGER.info("file [" + targetFile + "] is about to be overwritten");
                    }
                }
                LOGGER.info("file [" + sourceFile + "] downloading started");
                so.downloadObject(new File(targetFile));
                LOGGER.info("file [" + sourceFile + "] downloading completed, downloaded file at [" + targetFile + "]");

                ReturnInfo ri = new ReturnInfo()
                        .set(SOURCE_FILE_PATH, sourceFile)
                        .set(TARGET_FILE_PATH, targetFile);

                return Result.success().set(RETURN_INFO, ri);
            } else {
                LOGGER.info("file [" + sourceFile + "] doesn't exist and couldn't be downloaded");
                return Result.failure("file [" + sourceFile + "] doesn't exist and couldn't be downloaded");
            }
        } catch (Exception e) {
            LOGGER.error("file [" + sourceFile + "] downloading failed", e);
            return Result.failure("file [" + sourceFile + "] downloading failed due to " + e.getMessage());
        }
    }

    public Result copyFilesFrom(String containerName, String[] sourceFiles, String targetDirectory, boolean overwrite) {
        Result compositeResult = Result.composite(sourceFiles.length);
        for (String sourceFile : sourceFiles) {
            String targetFile = figureOutTargetFile(sourceFile, targetDirectory);
            Result result = copyFileFrom(containerName, sourceFile, targetFile, overwrite);
            compositeResult.addSubResult(result);
        }
        return compositeResult;
    }

    private String figureOutTargetFile(String sourceFile, String targetDirectory) {
        String shortFileName = Paths.get(sourceFile).getFileName().toString();
        return Paths.get(targetDirectory, shortFileName).toString();
    }

    public Result copyLocalFileTo(String containerName, String sourceFile, String targetFile, boolean overwrite, String ttl) {
        try {
            if (!Files.exists(Paths.get(sourceFile))) {
                LOGGER.error("No file exists at location: " + sourceFile);
                return Result.failure("no file exists at location: " + sourceFile);
            }

            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                return Result.failure("container [" + containerName + "] does not exist");
            }

            StoredObject so = container.getObject(targetFile);
            if (so.exists()) {
                LOGGER.info("file [" + targetFile + "] already exists in specified container");
                if (!overwrite) {
                    return Result.failure("file [" + targetFile + "] already exists in specified container");
                } else {
                    LOGGER.info("file [" + targetFile + "] is about to be overwritten");
                }
            }

            LOGGER.info("file [" + sourceFile + "] upload started");
            UploadInstructions uploadInstructions = new UploadInstructions(new File(sourceFile));
            long ttlInSeconds = 365 * 24 * 60 * 60;
            try {
                ttlInSeconds = Long.parseLong(ttl);
            } catch (Exception e) {
                LOGGER.info("missing ttl-in-seconds, use default ttl as 365 days");
            }
            uploadInstructions.setDeleteAfter(new DeleteAfter(ttlInSeconds));
            so.uploadObject(uploadInstructions);
            LOGGER.info("file [" + sourceFile + "] uploaded to [" + targetFile + "]");
            LOGGER.info("publicURL=" + so.getPublicURL());

            ReturnInfo ri = extractInfo(so)
                    .set(SOURCE_FILE_PATH, sourceFile)
                    .set(TARGET_FILE_PATH, targetFile);

            return Result.success().set(RETURN_INFO, ri);
        } catch (Exception e) {
            LOGGER.error("file [" + sourceFile + "] failed to be uploaded to [" + targetFile + "]", e);
            return Result.failure("file [" + sourceFile + "] failed to be uploaded to [" + targetFile + "] due to " + e.getMessage());
        }
    }

    public Result copyLocalFilesTo(String containerName, String[] sourceFiles, String targetDirectory, boolean overwrite, String ttl) {
        Result compositeResult = Result.composite(sourceFiles.length);
        for (String sourceFile : sourceFiles) {
            String targetFile = figureOutTargetFile(sourceFile, targetDirectory);
            Result result = copyLocalFileTo(containerName, sourceFile, targetFile, overwrite, ttl);
            compositeResult.addSubResult(result);
        }
        return compositeResult;
    }

    public Result deleteFile(String containerName, String file) {
        try {
            Container container = account.getContainer(containerName);
            if (!container.exists()) {
                LOGGER.error("container [" + containerName + "] doesn't exist");
                return Result.failure("container [" + containerName + "] does not exist");
            }

            StoredObject so = container.getObject(file);
            if (so.exists()) {
                LOGGER.info("file [" + file + "] deletion started");
                so.delete();
                LOGGER.info("file [" + file + "] deletion completed");
                return Result.success();
            } else {
                LOGGER.info("file [" + file + "] doesn't exist");
                return Result.success("file [" + file + "] doesn't exist");
            }
        } catch (Exception e) {
            LOGGER.error("file [" + file + "] deletion failed", e);
            return Result.failure("file [" + file + "] deletion failed due to " + e.getMessage());
        }
    }

    public Result deleteFiles(String containerName, String[] files) {
        Result compositeResult = Result.composite(files.length);
        compositeResult.set(RETURN_INFOS, new ReturnInfos());

        for (String file : files) {
            compositeResult.addSubResult(deleteFile(containerName, file));
        }
        return compositeResult;
    }

    private static ReturnInfo extractInfo(Container c) {
        ReturnInfo ri = new ReturnInfo();
        ri.set(CONTAINER_NAME, c.getName());
        ri.set(CONTAINER_PATH, c.getPath());
        ri.set(CONTAINER_BYTES_USED, c.getBytesUsed());
        ri.set(CONTAINER_OBJECT_COUNT, c.getCount());
        return ri;
    }

    private static ReturnInfo extractInfo(StoredObject so) {
        ReturnInfo ri = new ReturnInfo();
        ri.set(SO_NAME, so.getName());
        ri.set(SO_PATH, so.getPath());
        ri.set(SO_URL, so.getURL());
        ri.set(SO_PUBLIC_URL, so.getPublicURL());
        ri.set(SO_PRIVATE_URL, so.getPrivateURL());
        ri.set(SO_CONTENT_TYPE, so.getContentType());
        return ri;
    }

    private static String toString(Container c) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("name:").append(c.getName()).append(", ");
        sb.append("path:").append(c.getPath()).append(", ");
        sb.append("bytesUsed:").append(c.getBytesUsed()).append(", ");
        sb.append("fileCount:").append(c.getCount());
        sb.append('}');
        return sb.toString();
    }

    private static String toString(StoredObject so) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("name:").append(so.getName()).append(", ");
        sb.append("path:").append(so.getPath()).append(", ");
        sb.append("url:").append(so.getURL()).append(", ");
        sb.append("privateURL:").append(so.getPrivateURL()).append(", ");
        sb.append("publicURL:").append(so.getPublicURL()).append(", ");
        sb.append("contentType:").append(so.getContentType());
        sb.append('}');
        return sb.toString();
    }

}
