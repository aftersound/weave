package io.aftersound.weave.knox.command;

import io.aftersound.weave.command.CommandManual;
import io.aftersound.weave.command.CommandReference;

import java.util.Arrays;
import java.util.Collection;

public class KnoxCommandManual {

    static final CommandReference ListFile = CommandReference.of(
            "ls",
            "file"
    );

    private static final Collection<CommandReference> COMMAND_REFERENCES = Arrays.asList(
            ListFile
    );

    public static final CommandManual INSTANCE = new CommandManual(COMMAND_REFERENCES);
}
