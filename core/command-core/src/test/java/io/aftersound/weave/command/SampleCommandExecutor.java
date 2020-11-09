package io.aftersound.weave.command;

import io.aftersound.weave.common.Context;
import io.aftersound.weave.common.Key;

public class SampleCommandExecutor implements CommandExecutor {

    @Override
    public void execute(CommandHandle commandHandle, Context context) {
        Command command;
        try {
            command = commandHandle.command();
        } catch (CommandLineException e) {
            context
                    .set(Key.<String>of("ack"), "failure")
                    .set(Key.of("message"), e.getMessage())
                    .set(Key.of("hint"), e.getHelp());
            return;
        }

        String resource = command.getResource();
        String action = command.getAction();
        if ("container".equals(resource)) {
            if ("create".equals(action)) {
                createContainer(command, context);
            } else if ("delete".equals(action)) {
                deleteContainer(command, context);
            } else {
                throw new UnsupportedOperationException(action + " on " + resource + " is not supported");
            }
        } else {
            throw new UnsupportedOperationException(resource + " is not supported");
        }
    }

    private void createContainer(Command command, Context context) {
        context.set(Key.<String>of("ack"), "success").set(Key.<String>of("publicURL"), "http://localhost");
    }

    private void deleteContainer(Command command, Context context) {
        context.set(Key.<String>of("ack"), "success").set(Key.of("message"), "does-not-exist");
    }

}
