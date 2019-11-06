package io.aftersound.weave.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public final class CommandHandle {

    private final CommandReference commandReference;
    private final String commandLine;

    private CommandHandle(CommandReference commandReference, String commandLine) {
        this.commandReference = commandReference;
        this.commandLine = commandLine;
    }

    static CommandHandle of(CommandReference commandReference, String commandLine) {
        return new CommandHandle(commandReference, commandLine);
    }

    public String commandLine() {
        return commandLine;
    }

    public Command command() throws CommandLineException {
        if (commandReference == null) {
            throw new CommandLineException("'" + commandLine + "' could not be recognized");
        }

        CommandLine cmdLine;
        try {
            cmdLine = new DefaultParser().parse(commandReference.getOptions(), commandLine.split(" "));
        } catch (ParseException e) {
            throw new CommandLineException(e.getMessage(), commandReference.getHelp());
        }

        return new Command(commandLine, commandReference, cmdLine);
    }
}
