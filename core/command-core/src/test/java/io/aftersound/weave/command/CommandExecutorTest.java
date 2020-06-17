package io.aftersound.weave.command;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.Result;
import org.apache.commons.cli.Option;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CommandExecutorTest {

    @Test
    public void testExecuteSuccess1() {
        CommandReference cmdRef = CommandReference.of(
                "create",
                "container",
                Option.builder()
                        .longOpt("name")
                        .required()
                        .argName("containerName")
                        .desc("name of container to be created")
                        .hasArg()
                        .build()
        );
        CommandManual commandManual = new CommandManual(Arrays.asList(cmdRef));
        CommandHandle commandHandle = commandManual.getCommandHandle("create container --name test");
        assertEquals("create container --name test", commandHandle.commandLine());

        Result result = new SampleCommandExecutor().execute(commandHandle);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.keys().size());
        for (String key : result.keys()) {
            assertEquals("http://localhost", result.get(Key.<String>of(key)));
            assertNull(result.get(Key.<Integer>of(key)));
        }
    }

    @Test
    public void testExecuteSuccess2() {
        CommandReference cmdRef = CommandReference.of(
                "delete",
                "container",
                Option.builder()
                        .longOpt("name")
                        .required()
                        .argName("containerName")
                        .desc("name of container to be created")
                        .hasArg()
                        .build()
        );
        CommandManual commandManual = new CommandManual(Arrays.asList(cmdRef));
        CommandHandle commandHandle = commandManual.getCommandHandle("delete container --name test");
        assertEquals("delete container --name test", commandHandle.commandLine());
        Result result = new SampleCommandExecutor().execute(commandHandle);
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("does-not-exist", result.getMessage());
    }

    @Test
    public void testExecuteFailure() {
        CommandReference cmdRef = CommandReference.of(
                "create",
                "container",
                Option.builder()
                        .longOpt("name")
                        .required()
                        .argName("containerName")
                        .desc("name of container to be created")
                        .hasArg()
                        .build()
        );
        CommandManual commandManual = new CommandManual(Arrays.asList(cmdRef));
        CommandHandle commandHandle = commandManual.getCommandHandle("create container --container-name test");
        assertEquals("create container --container-name test", commandHandle.commandLine());
        Result result = new SampleCommandExecutor().execute(commandHandle);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getFailureReason().contains("Unrecognized option: --container-name"));
        assertNotNull(result.getHint());
        assertTrue(result.getHint().contains("usage: create container"));
    }

}