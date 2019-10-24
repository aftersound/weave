package io.aftersound.weave.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Test;

import static org.junit.Assert.*;

public class ApacheCommandCliTest {

    @Test
    public void testOptionsAndParser() throws Exception {
        // to understand the basic mechanic of apache command cli
        String instruction = "upload file -c test -s /tmp/data/file1 -t file2 --overwrite-if-exist --make-public";

        Options options = new Options();

        Option o = Option.builder("c").required().argName("sourceFile").hasArg().desc("name of container to be created").build();
        options.addOption(o);

        o = Option.builder("s").required().numberOfArgs(1).desc("source file to be uploaded").build();
        options.addOption(o);

        o = Option.builder("t").required().numberOfArgs(1).desc("target file in specified container").build();
        options.addOption(o);

        o = Option.builder("o").longOpt("overwrite-if-exist").desc("instruct swift to overwrite if target file already exists").build();
        options.addOption(o);

        o = Option.builder("p").longOpt("make-public").desc("instruct swift to make target file publicly accessible").build();
        options.addOption(o);

        CommandLine cmdLine = new DefaultParser().parse(options, instruction.split(" "));
        assertEquals("upload", cmdLine.getArgs()[0]);
        assertEquals("file", cmdLine.getArgs()[1]);
        assertEquals("test", cmdLine.getOptionValue("c"));
        assertEquals("/tmp/data/file1", cmdLine.getOptionValue("s"));
        assertEquals("file2", cmdLine.getOptionValue("t"));
        assertTrue(cmdLine.hasOption("o"));
        assertTrue(cmdLine.hasOption("p"));
    }

}