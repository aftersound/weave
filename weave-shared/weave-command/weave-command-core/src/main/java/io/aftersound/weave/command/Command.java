package io.aftersound.weave.command;

import org.apache.commons.cli.CommandLine;

public class Command {

    private final String cmdLinee;
    private final CommandReference commandReference;
    private final CommandLine commandLine;

    Command(String cmdLinee, CommandReference cmdRef, CommandLine commandLine) {
        this.cmdLinee = cmdLinee;
        this.commandReference = cmdRef;
        this.commandLine = commandLine;
    }

    public CommandReference getCommandReference() {
        return this.commandReference;
    }

    public String getAction() {
        return commandLine.getArgs()[0];
    }

    public String getResource() {
        return commandLine.getArgs()[1];
    }

    public boolean hasOption(String opt) {
        return commandLine.hasOption(opt);
    }

    public String getOptionValue(String opt) {
        return commandLine.getOptionValue(opt);
    }

    public String getOptionValue(String opt, String defaultValue) {
        return commandLine.getOptionValue(opt, defaultValue);
    }

    public String[] getOptionValues(String opt) {
        return commandLine.getOptionValues(opt);
    }

}
