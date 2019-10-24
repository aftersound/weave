package io.aftersound.weave.knox;

import io.aftersound.weave.common.Result;
import org.apache.knox.gateway.shell.KnoxSession;
import org.apache.knox.gateway.shell.hdfs.Hdfs;

import java.util.List;

public class KnoxSessionDelegate {

    private final KnoxSession session;

    public KnoxSessionDelegate(KnoxSession session) {
        this.session = session;
    }

    public Result listFiles(String directory) {
        String json = Hdfs.ls(session).dir("/user/guest/example").now().toString();
        // TODO
        return Result.failure("Unimplemented");
    }

    public Result copyFileFrom(String sourceFilePath, String targetFilePath) {
        // TODO
        Hdfs.get(session).from(sourceFilePath).now();
        return Result.failure("Unimplemented");
    }

    public Result copyFilesFrom(List<String> sourceFilePaths, String targetDirectory) {
        return Result.failure("Unimplemented");
    }

    public Result deleteFile(String filePath) {
        Hdfs.rm(session).file(filePath).now();
        return Result.failure("Unimplemented");
    }

    public Result copyLocalFileTo(String sourceFilePath, String targetFilePath) {
        Hdfs.put(session).file(sourceFilePath).to(targetFilePath).now();
        return Result.failure("Unimplemented");
    }

    public Result copyLocalFilesTo(String[] strings, String targetDirectory) {
        return Result.failure("Unimplemented");
    }
}
