package io.aftersound.weave.swift.command;

import io.aftersound.weave.command.CommandManual;
import io.aftersound.weave.command.CommandReference;
import org.apache.commons.cli.Option;

import java.util.Arrays;
import java.util.Collection;

public final class SwiftCommandManual {

    static final CommandReference GetContainerInfo = CommandReference.of(
            "get",
            "container",
            Option.builder()
                    .longOpt("name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build()
    );

    static final CommandReference CreateContainer = CommandReference.of(
            "create",
            "container",
            Option.builder()
                    .longOpt("name")
                    .required()
                    .desc("name of container to be created")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder()
                    .longOpt("make-public")
                    .required(false)
                    .desc("instruct swift to make target container publicly accessible")
                    .hasArg(false)
                    .argName("makePublic")
                    .build(),
            Option.builder()
                    .longOpt("make-private")
                    .required(false)
                    .desc("instruct swift to make target container publicly inaccessible")
                    .hasArg(false)
                    .argName("makePrivate")
                    .build()
    );

    static final CommandReference DeleteContainer = CommandReference.of(
            "delete",
            "container",
            Option.builder()
                    .longOpt("name")
                    .required()
                    .desc("name of container to be deleted")
                    .hasArg()
                    .argName("containerName")
                    .build()
    );

    static final CommandReference CreateContainers = CommandReference.of(
            "create",
            "containers",
            Option.builder()
                    .longOpt("names")
                    .required()
                    .desc("names of containers to be deleted")
                    .hasArgs()
                    .argName("containerNames")
                    .valueSeparator(',')
                    .build()
    );

    static final CommandReference DeleteContainers = CommandReference.of(
            "delete",
            "containers",
            Option.builder()
                    .longOpt("names")
                    .required()
                    .desc("names of containers to be deleted")
                    .hasArgs()
                    .argName("containerNames")
                    .valueSeparator(',')
                    .build()
    );

    static final CommandReference ListContainers = CommandReference.of(
            "list",
            "containers"
    );

    static final CommandReference GetFileInfo = CommandReference.of(
            "get",
            "file",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("f")
                    .longOpt("file-name")
                    .required()
                    .desc("file in interest")
                    .hasArg()
                    .argName("fileName")
                    .build(),
            Option.builder("i")
                    .longOpt("information")
                    .desc("information of file in interest")
                    .hasArgs()
                    .argName("information")
                    .valueSeparator(',')
                    .build()
    );

    static final CommandReference UploadFile = CommandReference.of(
            "upload",
            "file",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("s")
                    .longOpt("source-file")
                    .required()
                    .desc("source file path in local file system")
                    .hasArg()
                    .argName("sourceFile")
                    .build(),
            Option.builder("t")
                    .longOpt("target-file")
                    .required()
                    .desc("target file path in specified swift container")
                    .hasArg()
                    .argName("targetFile")
                    .build(),
            Option.builder()
                    .longOpt("overwrite-if-exist")
                    .required(false)
                    .desc("instruct swift to overwrite if specified container already has target file")
                    .hasArg(false)
                    .argName("overwriteIfExist")
                    .build(),
            Option.builder()
                    .longOpt("ttl-in-seconds")
                    .required(false)
                    .desc("instruct swift to delete file after ttl")
                    .hasArg()
                    .argName("ttlInSeconds")
                    .type(Long.class)
                    .build()
    );

    static final CommandReference DownloadFile = CommandReference.of(
            "download",
            "file",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("s")
                    .longOpt("source-file")
                    .required()
                    .desc("source file path in specified container")
                    .hasArg()
                    .argName("sourceFile")
                    .build(),
            Option.builder("t")
                    .longOpt("target-file")
                    .required()
                    .desc("target file path in local file system")
                    .hasArg()
                    .argName("targetFile")
                    .build(),
            Option.builder()
                    .longOpt("overwrite-if-exist")
                    .required(false)
                    .desc("instruct to overwrite if local file system already has target file")
                    .hasArg(false)
                    .argName("overwriteIfExist")
                    .build()
    );

    static final CommandReference DeleteFile = CommandReference.of(
            "delete",
            "file",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("f")
                    .longOpt("file")
                    .required()
                    .desc("target file in specified container to be deleted")
                    .hasArg()
                    .argName("file")
                    .build()
    );

    static final CommandReference UploadFiles = CommandReference.of(
            "upload",
            "files",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("s")
                    .longOpt("source-files")
                    .required()
                    .desc("source files in local file system")
                    .hasArgs()
                    .argName("sourceFiles")
                    .valueSeparator(',')
                    .build(),
            Option.builder("t")
                    .longOpt("target-directory")
                    .required()
                    .desc("target directory in specified swift container")
                    .hasArg()
                    .argName("targetDirectory")
                    .build(),
            Option.builder()
                    .longOpt("overwrite-if-exist")
                    .required(false)
                    .desc("instruct swift to overwrite if specified container already has target files")
                    .hasArg(false)
                    .argName("overwriteIfExist")
                    .build(),
            Option.builder()
                    .longOpt("ttl-in-seconds")
                    .required(false)
                    .desc("instruct swift to delete the file after specified time")
                    .hasArg()
                    .argName("ttlInSeconds")
                    .type(Long.class)
                    .build()
    );

    private static final CommandReference DownloadFiles = CommandReference.of(
            "download",
            "files",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("s")
                    .longOpt("source-files")
                    .required()
                    .desc("source files in specified container")
                    .hasArgs()
                    .argName("sourceFiles")
                    .valueSeparator(',')
                    .build(),
            Option.builder("t")
                    .longOpt("target-directory")
                    .required()
                    .desc("target directory in local file system")
                    .hasArg()
                    .argName("targetDirectory")
                    .build(),
            Option.builder()
                    .longOpt("overwrite-if-exist")
                    .required(false)
                    .desc("instruct to overwrite if local file system already has target files")
                    .hasArg(false)
                    .argName("overwriteIfExist")
                    .build()
    );

    static final CommandReference DeleteFiles = CommandReference.of(
            "delete",
            "files",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build(),
            Option.builder("f")
                    .longOpt("files")
                    .required()
                    .desc("target files in specified container to be deleted")
                    .hasArgs()
                    .argName("files")
                    .valueSeparator(',')
                    .build()
    );

    static final CommandReference ListFiles = CommandReference.of(
            "list",
            "files",
            Option.builder("c")
                    .longOpt("container-name")
                    .required()
                    .desc("name of target container")
                    .hasArg()
                    .argName("containerName")
                    .build()
    );

    private static final Collection<CommandReference> COMMAND_REFERENCES = Arrays.asList(
            GetContainerInfo,
            CreateContainer,
            DeleteContainer,
            CreateContainers,
            DeleteContainers,
            ListContainers,
            GetFileInfo,
            UploadFile,
            DownloadFile,
            DeleteFile,
            UploadFiles,
            DownloadFiles,
            DeleteFiles,
            ListFiles
    );

    public static final CommandManual INSTANCE = new CommandManual(COMMAND_REFERENCES);

}
