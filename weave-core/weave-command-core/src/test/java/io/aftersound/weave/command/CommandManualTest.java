package io.aftersound.weave.command;

import org.apache.commons.cli.Option;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

public class CommandManualTest {

    @Test(expected = CommandLineException.class)
    public void createCommandNotSupportedAction() throws Exception {
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
        commandManual.getCommandHandle("delete container --name test").command();
    }

    @Test(expected = CommandLineException.class)
    public void createCommandMissingAction() throws Exception {
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
        commandManual.getCommandHandle(null).command();
    }

    @Test(expected = CommandLineException.class)
    public void createCommandMissingResource() throws Exception {
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
        commandManual.getCommandHandle("create ").command();
    }

    @Test(expected = CommandLineException.class)
    public void createCommandInvalidOption() throws Exception {
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
        commandManual.getCommandHandle("create container --container-name test").command();
    }

    @Test
    public void createCommandHelpFromException() {
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

        CommandLineException ex = null;
        try {
            commandManual.getCommandHandle("create container --container-name test").command();
        } catch (CommandLineException e) {
            ex = e;
        }
        System.out.println(ex.getMessage());
        System.out.println(ex.getHelp());
        assertNotNull(ex);
        assertTrue(ex.getMessage().contains("containerName"));
        assertTrue(ex.getHelp().contains("containerName"));
    }

    @Test
    public void createCommand() throws Exception {
        Collection<CommandReference> cmdRefs = Arrays.asList(
                CommandReference.of(
                        "create",
                        "container",
                        Option.builder()
                                .longOpt("name")
                                .required()
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build(),
                        Option.builder()
                                .longOpt("make-public")
                                .required(false)
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build(),
                        Option.builder()
                                .longOpt("make-private")
                                .required(false)
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build()
                )
        );
        CommandManual commandManual = new CommandManual(cmdRefs);
        Command command = commandManual.getCommandHandle("create container --name test --make-private true").command();
        assertEquals("create", command.getAction());
        assertEquals("container", command.getResource());
        assertEquals("test", command.getOptionValue("name"));
        assertTrue(command.hasOption("name"));
        assertFalse(command.hasOption("container-name"));
        assertEquals(1, command.getOptionValues("name").length);
        assertTrue(command.getCommandReference().getHelp().contains("containerName"));
        assertEquals("false", command.getOptionValue("make-public", "false"));
        assertEquals("true", command.getOptionValue("make-private"));
    }

    @Test
    public void createCommands() throws Exception {
        Collection<CommandReference> cmdRefs = Arrays.asList(
                CommandReference.of(
                        "create",
                        "container",
                        Option.builder()
                                .longOpt("name")
                                .required()
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build(),
                        Option.builder()
                                .longOpt("make-public")
                                .required(false)
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build(),
                        Option.builder()
                                .longOpt("make-private")
                                .required(false)
                                .argName("containerName")
                                .desc("name of container to be created")
                                .hasArg()
                                .build()
                )
        );
        CommandManual commandManual = new CommandManual(cmdRefs);

        Collection<CommandHandle> commandHandles = commandManual.getCommandHandles(null);
        assertNotNull(commandHandles);
        assertEquals(0, commandHandles.size());

        commandHandles = commandManual.getCommandHandles(new String[0]);
        assertNotNull(commandHandles);
        assertEquals(0, commandHandles.size());

        commandHandles = commandManual.getCommandHandles("create container --name test --make-private true");
        assertEquals(1, commandHandles.size());
        Command command = commandHandles.iterator().next().command();
        assertEquals("create", command.getAction());
        assertEquals("container", command.getResource());
        assertEquals("test", command.getOptionValue("name"));
        assertTrue(command.hasOption("name"));
        assertFalse(command.hasOption("container-name"));
        assertEquals(1, command.getOptionValues("name").length);
        assertTrue(command.getCommandReference().getHelp().contains("containerName"));
        assertEquals("false", command.getOptionValue("make-public", "false"));
        assertEquals("true", command.getOptionValue("make-private"));
    }

}