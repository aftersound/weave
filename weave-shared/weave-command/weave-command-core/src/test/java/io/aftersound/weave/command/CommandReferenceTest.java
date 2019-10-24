package io.aftersound.weave.command;

import org.apache.commons.cli.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandReferenceTest {

    @Test
    public void testOf() throws Exception {
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
        assertEquals("create", cmdRef.getAction());
        assertEquals("container", cmdRef.getResource());
        assertEquals(1, cmdRef.getOptions().getOptions().size());
        assertEquals(1, cmdRef.getOptions().getRequiredOptions().size());
        assertTrue(cmdRef.getHelp().contains("containerName"));
    }

}