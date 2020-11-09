package io.aftersound.weave.command;

import io.aftersound.weave.common.Context;
import io.aftersound.weave.common.Key;
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

        Context context = new Context();
        new SampleCommandExecutor().execute(commandHandle, context);
        assertNotNull(context.get(Key.<String>of("ack")));
        assertEquals("success", context.get(Key.<String>of("ack")));
        assertEquals("http://localhost", context.get(Key.<String>of("publicURL")));
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

        Context context = new Context();
        new SampleCommandExecutor().execute(commandHandle, context);
        assertNotNull(context.get(Key.<String>of("ack")));
        assertEquals("success", context.get(Key.<String>of("ack")));
        assertEquals("does-not-exist", context.get(Key.<String>of("message")));
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

        Context context = new Context();
        new SampleCommandExecutor().execute(commandHandle, context);
        assertNotNull(context.get(Key.<String>of("ack")));
        assertEquals("failure", context.get(Key.<String>of("ack")));
        assertTrue(context.get(Key.<String>of("message")).contains("Unrecognized option: --container-name"));
        assertTrue(context.get(Key.<String>of("hint")).contains("usage: create container"));
    }

}