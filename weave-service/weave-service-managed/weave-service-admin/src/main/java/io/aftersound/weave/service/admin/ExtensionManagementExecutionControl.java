package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import java.util.List;

public class ExtensionManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "ExtensionManagement",
            ExtensionManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String extensionDirectory;
    private List<String> extensionCategories;

    public String getExtensionDirectory() {
        return extensionDirectory;
    }

    public void setExtensionDirectory(String extensionDirectory) {
        this.extensionDirectory = extensionDirectory;
    }

    public List<String> getExtensionCategories() {
        return extensionCategories;
    }

    public void setExtensionCategories(List<String> extensionCategories) {
        this.extensionCategories = extensionCategories;
    }
}
