package io.aftersound.weave.service.batch;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import java.util.List;

public class JobRunServiceExecutionControl implements ExecutionControl {

    public static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "JobRun",
            JobRunServiceExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String batchLibraryDirectory;
    private String batchExtensionDirectory;

    private List<String> commands;

    public String getBatchLibraryDirectory() {
        return batchLibraryDirectory;
    }

    public void setBatchLibraryDirectory(String batchLibraryDirectory) {
        this.batchLibraryDirectory = batchLibraryDirectory;
    }

    public String getBatchExtensionDirectory() {
        return batchExtensionDirectory;
    }

    public void setBatchExtensionDirectory(String batchExtensionDirectory) {
        this.batchExtensionDirectory = batchExtensionDirectory;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}
