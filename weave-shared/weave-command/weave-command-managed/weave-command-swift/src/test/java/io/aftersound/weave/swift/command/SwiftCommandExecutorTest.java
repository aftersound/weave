package io.aftersound.weave.swift.command;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.command.CommandHandle;
import io.aftersound.weave.common.Keys;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.swift.SwiftAccountFactory;
import io.aftersound.weave.utils.OptionsBuilder;
import org.javaswift.joss.model.Account;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SwiftCommandExecutorTest {

    private static Account account;

    @BeforeClass
    public static void oneTimeSetup() {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        SwiftAccountFactory factory = new SwiftAccountFactory(new DataClientRegistry(dcfBindings));

        Map<String, Object> options = new OptionsBuilder()
                .option("tenantId", "test")
                .option("tenantName", "test")
                .option("authUrl", "http://localhost:5000/v2.0/tokens")
                .option("username", "test")
                .option("password", "password")
                .option("preferredRegion", "Region1")
                .option("sslValidationDisabled", "true")
                .option("mockEnabled", "true")
                .build();
        account = factory.create("SwiftCommandExecutorTest", options);
    }

    private static Result execute(String commandLine) {
        CommandHandle commandHandle = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine);
        return new SwiftCommandExecutor(account).execute(commandHandle);
    }

    @Test
    public void executeInvalidCommand() {
        final String commandLine = "creatttte container --name test --make-public";
        Result result = execute(commandLine);
        assertFalse(result.isSuccess());
    }

    @Test
    public void executeGetContainer() {
        final String commandLine = "get container --name test";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeCreateContainer() {
        final String commandLine = "create container --name test --make-public";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeDeleteContainer() {
        final String commandLine = "delete container --name tbd";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeCreateContainers() {
        final String commandLine = "create containers --names test1,test2";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
        List<Result> results = result.get(Keys.SUB_RESULTS).all();
        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
    }

    @Test
    public void executeDeleteContainers() {
        final String commandLine = "delete containers --names test1,test2";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
        List<Result> results = result.get(Keys.SUB_RESULTS).all();
        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
    }

    @Test
    public void executeListContainers() {
        final String commandLine = "list containers";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeUploadFile() {
        final String commandLine = "upload file -c test -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t targetFile --overwrite-if-exist --ttl-in-seconds 86400";
        Result result = execute(commandLine);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeUploadFileFailure() {
        final String commandLine = "upload file -c test -s nonExistingSourceFile -t targetFile --overwrite-if-exist --ttl-in-seconds 86400";
        Result result = execute(commandLine);
        assertTrue(result.isFailure());
        assertEquals("no file exists at location: nonExistingSourceFile", result.getFailureReason());
    }

    @Test
    public void executeDownloadFile() throws IOException {
        // 1.create container
        execute("create container --name c4download");

        // 2. make sure container has file to be downloaded
        execute("upload file -c c4download -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toBeDownloaded");

        // 3.download
        Path dir = Files.createTempDirectory("SwiftCommandExecutorTest_executeDownloadFile");
        String targetFile = Paths.get(dir.toString(), "targetFile").toString();
        Result result = execute("download file -c c4download -s toBeDownloaded -t " + targetFile);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeDeleteFile() {
        // 1.create container
        execute("create container --name c4delete");

        // 2. make sure container has file to be downloaded
        execute("upload file -c c4delete -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toDelete");

        // 3.delete
        Result result = execute("delete file -c c4delete -f toDelete");
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeGetFile() throws IOException {
        // 1.create container
        execute("create container --name c4get");

        // 2. make sure container has file to be downloaded
        execute("upload file -c c4get -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toGet");

        // 3.get
        Result result = execute("get file -c c4get -f toGet");
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeUploadFiles() throws IOException {
        // 1.create container
        execute("create container --name c4uploads");

        // 2.upload
        String sourceFile = SwiftCommandExecutorTest.class.getResource("sourceFile").getFile();
        Result result = execute("upload files -c c4uploads -s " + sourceFile + " -t /dir");
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeDownloadFiles() throws IOException {
        // 1.create container
        execute("create container --name c4downloads");

        // 2.make sure container has files to be downloaded
        execute("upload file -c c4downloads -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toDownload1");

        execute("upload file -c c4downloads -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toDownload2");

        // 3.download
        String targetDir = Files.createTempDirectory("SwiftCommandExecutorTest_executeDownloadFiles").toString();
        Result result = execute("download files -c c4downloads --overwrite-if-exist -s toDownload1,toDownload2 -t " + targetDir);
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeDeleteFiles() throws IOException {
        // 1.create container
        execute("create container --name c4deletes");

        // 2. make sure container has files to be deleted
        execute("upload file -c c4deletes -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toDelete1");

        execute("upload file -c c4deletes -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toDelete2");

        // 2.delete
        Result result = execute("delete files -c c4deletes -f toDelete1,toDelete2");
        assertTrue(result.isSuccess());
    }

    @Test
    public void executeListFiles() throws IOException {
        // 1.create container
        execute("create container --name c4lists");

        // 2. make sure container has files to be deleted
        execute("upload file -c c4lists -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toList1");

        execute("upload file -c c4lists -s " +
                SwiftCommandExecutorTest.class.getResource("sourceFile").getFile() +
                " -t toList2");

        // 2.list
        Result result = execute("list files -c c4lists");
        assertTrue(result.isSuccess());
    }

}