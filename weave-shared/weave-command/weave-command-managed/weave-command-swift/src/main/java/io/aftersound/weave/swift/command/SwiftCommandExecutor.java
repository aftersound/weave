package io.aftersound.weave.swift.command;

import io.aftersound.weave.command.Command;
import io.aftersound.weave.command.CommandExecutor;
import io.aftersound.weave.command.CommandHandle;
import io.aftersound.weave.command.CommandLineException;
import io.aftersound.weave.common.Result;
import io.aftersound.weave.swift.SwiftAccountDelegate;
import org.javaswift.joss.model.Account;

public class SwiftCommandExecutor implements CommandExecutor {

    private final SwiftAccountDelegate delegate;

    public SwiftCommandExecutor(Account account) {
        this.delegate = new SwiftAccountDelegate(account);
    }

    @Override
    public Result execute(CommandHandle commandHandle) {
        Command command;
        try {
            command = commandHandle.command();
        } catch (CommandLineException cliException) {
            return Result.failure(cliException).setHint(cliException.getHelp());
        }

        Resource resource = Resource.valueOf(command.getResource());
        Action action = Action.valueOf(command.getAction());

        switch (resource) {
            case container:
                switch (action) {
                    case create:
                        return createContainer(command);
                    case delete:
                        return deleteContainer(command);
                    case get:
                        return getContainerInfo(command);
                    default:
                }
            case containers:
                switch (action) {
                    case create:
                        return createContainers(command);
                    case delete:
                        return deleteContainers(command);
                    case list:
                        return listContainers(command);
                    default:
                }
            case file:
                switch (action) {
                    case upload:
                        return uploadFile(command);
                    case download:
                        return downloadFile(command);
                    case delete:
                        return deleteFile(command);
                    case get:
                        return getFileInfo(command);
                    default:
                }
            case files:
                switch (action) {
                    case upload:
                        return uploadFiles(command);
                    case download:
                        return downloadFiles(command);
                    case delete:
                        return deleteFiles(command);
                    case list:
                        return listFiles(command);
                    default:
                }
            default:
        }

        return Result.failure(action + " on " + resource + " is not supported");
    }

    private Result createContainer(Command command) {
        return delegate.createContainer(command.getOptionValue("name"), command.hasOption("make-public"));
    }

    private Result deleteContainer(Command command) {
        return delegate.deleteContainer(command.getOptionValue("name"));
    }

    private Result getContainerInfo(Command command) {
        return delegate.getContainerInfo(command.getOptionValue("name"));
    }

    private Result createContainers(Command command) {
        String[] containerNames = command.getOptionValues("names");
        boolean makePublic = command.hasOption("make-public");
        return delegate.createContainers(containerNames, makePublic);
    }

    private Result deleteContainers(Command command) {
        String[] containerNames = command.getOptionValues("names");
        return delegate.deleteContainers(containerNames);
    }

    private Result listContainers(Command command) {
        return delegate.listContainers();
    }

    private Result uploadFile(Command command) {
        String containerName = command.getOptionValue("c");
        String sourceFile = command.getOptionValue("s");
        String targetFile = command.getOptionValue("t");
        boolean overwrite = command.hasOption("overwrite-if-exist");
        String ttl = command.getOptionValue("ttl-in-seconds");

        return delegate.copyLocalFileTo(containerName, sourceFile, targetFile, overwrite, ttl);
    }

    private Result downloadFile(Command command) {
        String containerName = command.getOptionValue("c");
        String sourceFile = command.getOptionValue("s");
        String targetFile = command.getOptionValue("t");
        boolean overwrite = command.hasOption("overwrite-if-exist");
        return delegate.copyFileFrom(containerName, sourceFile, targetFile, overwrite);
    }

    private Result deleteFile(Command command) {
        String containerName = command.getOptionValue("c");
        String file = command.getOptionValue("f");
        return delegate.deleteFile(containerName, file);
    }

    private Result getFileInfo(Command command) {
        String containerName = command.getOptionValue("c");
        String file = command.getOptionValue("f");
        return delegate.getFileInfo(containerName, file);
    }

    private Result uploadFiles(Command command) {
        String containerName = command.getOptionValue("c");
        String[] sourceFiles = command.getOptionValues("s");
        String targetDirectory = command.getOptionValue("t");
        boolean overwrite = command.hasOption("overwrite-if-exist");
        String ttl = command.getOptionValue("ttl-in-seconds");
        return delegate.copyLocalFilesTo(containerName, sourceFiles, targetDirectory, overwrite, ttl);
    }

    private Result downloadFiles(Command command) {
        String containerName = command.getOptionValue("c");
        String[] sourceFiles = command.getOptionValues("s");
        String targetDirectory = command.getOptionValue("t");
        boolean overwrite = command.hasOption("overwrite-if-exist");
        return delegate.copyFilesFrom(containerName, sourceFiles, targetDirectory, overwrite);
    }

    private Result deleteFiles(Command command) {
        String containerName = command.getOptionValue("c");
        String[] files = command.getOptionValues("f");
        return delegate.deleteFiles(containerName, files);
    }

    private Result listFiles(Command command) {
        return delegate.listFiles(command.getOptionValue("c"));
    }

}
