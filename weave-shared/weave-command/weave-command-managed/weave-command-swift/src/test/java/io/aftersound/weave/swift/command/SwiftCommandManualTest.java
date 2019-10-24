package io.aftersound.weave.swift.command;

import io.aftersound.weave.command.Command;
import io.aftersound.weave.command.CommandLineException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SwiftCommandManualTest {

    @Test
    public void testGetContainer() throws CommandLineException {
        final String commandLine = "get container --name test";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("name"));
    }

    @Test
    public void testCreateContainer() throws CommandLineException {
        final String commandLine = "create container --name test --make-public";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("name"));
        assertTrue(command.hasOption("make-public"));
    }

    @Test
    public void testDeleteContainer() throws CommandLineException {
        final String commandLine = "delete container --name test";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("name"));
    }

    @Test
    public void testCreateContainers() throws CommandLineException {
        final String commandLine = "create containers --names test1,test2";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals(2, command.getOptionValues("names").length);
        assertEquals("test1", command.getOptionValues("names")[0]);
        assertEquals("test2", command.getOptionValues("names")[1]);
    }

    @Test
    public void testDeleteContainers() throws CommandLineException {
        final String commandLine = "delete containers --names test1,test2";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals(2, command.getOptionValues("names").length);
        assertEquals("test1", command.getOptionValues("names")[0]);
        assertEquals("test2", command.getOptionValues("names")[1]);
    }

    @Test
    public void testUploadFile() throws CommandLineException {
        final String commandLine = "upload file -c test -s /tmp/data/sourceFile -t targetFile --overwrite-if-exist --ttl-in-seconds 86400";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals("/tmp/data/sourceFile", command.getOptionValue("s"));
        assertEquals("targetFile", command.getOptionValue("t"));
        assertTrue(command.hasOption("overwrite-if-exist"));
        assertFalse(command.hasOption("make-public"));
        assertTrue(command.hasOption("ttl-in-seconds"));
        assertEquals("86400", command.getOptionValue("ttl-in-seconds"));
    }

    @Test
    public void testDownloadFile() throws CommandLineException {
        final String commandLine = "download file -c test -s sourceFile -t /tmp/data/targetFile --overwrite-if-exist";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals("sourceFile", command.getOptionValue("s"));
        assertEquals("/tmp/data/targetFile", command.getOptionValue("t"));
        assertTrue(command.hasOption("overwrite-if-exist"));
    }

    @Test
    public void testDeleteFile() throws CommandLineException {
        final String commandLine = "delete file -c test -f fileToBeDeleted";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals("fileToBeDeleted", command.getOptionValue("f"));
    }

    @Test
    public void testUploadFiles() throws CommandLineException {
        final String commandLine = "upload files -c test -s /tmp/data/sourceFile1,/tmp/data/sourceFile2 -t targetDirectory --overwrite-if-exist --ttl-in-seconds 86400";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals(2, command.getOptionValues("s").length);
        assertEquals("/tmp/data/sourceFile1", command.getOptionValues("s")[0]);
        assertEquals("/tmp/data/sourceFile2", command.getOptionValues("s")[1]);
        assertEquals("targetDirectory", command.getOptionValue("t"));
        assertTrue(command.hasOption("overwrite-if-exist"));
        assertTrue(command.hasOption("ttl-in-seconds"));
        assertEquals("86400", command.getOptionValue("ttl-in-seconds"));
    }

    @Test
    public void testDownloadFiles() throws CommandLineException {
        final String commandLine = "download files -c test -s sourceFile1,sourceFile2 -t /tmp/data/";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals(2, command.getOptionValues("s").length);
        assertEquals("sourceFile1", command.getOptionValues("s")[0]);
        assertEquals("sourceFile2", command.getOptionValues("s")[1]);
        assertEquals("/tmp/data/", command.getOptionValue("t"));
        assertFalse(command.hasOption("overwrite-if-exist"));
    }

    @Test
    public void testDeleteFiles() throws CommandLineException {
        final String commandLine = "delete files -c test -f fileToBeDeleted1,fileToBeDeleted2";
        Command command = SwiftCommandManual.INSTANCE.getCommandHandle(commandLine).command();
        assertNotNull(command);
        assertEquals("test", command.getOptionValue("c"));
        assertEquals(2, command.getOptionValues("f").length);
        assertEquals("fileToBeDeleted1", command.getOptionValues("f")[0]);
        assertEquals("fileToBeDeleted2", command.getOptionValues("f")[1]);
    }

}