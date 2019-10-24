package io.aftersound.weave.command;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.Result;

public class SampleCommandExecutor implements CommandExecutor {

    @Override
    public Result execute(CommandHandle commandHandle) {
        Command command;
        try {
            command = commandHandle.command();
        } catch (CommandLineException e) {
            return Result.failure(e).setHint(e.getHelp());
        }

        String resource = command.getResource();
        String action = command.getAction();
        if ("container".equals(resource)) {
            if ("create".equals(action)) {
                return createContainer(command);
            } else if ("delete".equals(action)) {
                return deleteContainer(command);
            } else {
                throw new UnsupportedOperationException(action + " on " + resource + " is not supported");
            }
        } else {
            throw new UnsupportedOperationException(resource + " is not supported");
        }
    }

    private Result createContainer(Command command) {
        return Result.success().set(Key.as("publicURL", String.class), "http://localhost");
    }

    private Result deleteContainer(Command command) {
        return Result.success("does-not-exist");
    }

}
