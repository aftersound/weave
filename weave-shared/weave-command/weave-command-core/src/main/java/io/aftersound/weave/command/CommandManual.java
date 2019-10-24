package io.aftersound.weave.command;

import java.util.*;

public final class CommandManual {

    private final Map<String, CommandReference> byActionAndResource;

    public CommandManual(Collection<CommandReference> commandReferences) {
        this.byActionAndResource = createByActionAndResourceView(commandReferences);
    }

    private static Map<String, CommandReference> createByActionAndResourceView(Collection<CommandReference> commandReferences) {
        Map<String, CommandReference> byActionAndResource = new LinkedHashMap<>();
        for (CommandReference cmdRef : commandReferences) {
            byActionAndResource.put(cmdRef.getAction() + " " + cmdRef.getResource(), cmdRef);
        }
        return Collections.unmodifiableMap(byActionAndResource);
    }

    public CommandHandle getCommandHandle(String commandLine) {
        CommandReference cmdRef = findReference(commandLine);
        return CommandHandle.of(cmdRef, commandLine);
    }

    public Collection<CommandHandle> getCommandHandles(String ...commandLines) {
        if (commandLines == null || commandLines.length == 0) {
            return Collections.emptyList();
        }
        Collection<CommandHandle> commandHandles = new ArrayList<>();
        for (String commandLine : commandLines) {
            commandHandles.add(getCommandHandle(commandLine));
        }
        return commandHandles;
    }

    private CommandReference findReference(String commandLine) {
        return byActionAndResource.get(getActionAndResource(commandLine));
    }

    private static String getActionAndResource(String commandLine) {
        if (commandLine == null) {
            return null;
        }

        String[] splitted = commandLine.split(" ");
        if (splitted.length < 2) {
            return null;
        }

        String action = splitted[0];
        String resource = splitted[1];
        return action + " " + resource;
    }
}
