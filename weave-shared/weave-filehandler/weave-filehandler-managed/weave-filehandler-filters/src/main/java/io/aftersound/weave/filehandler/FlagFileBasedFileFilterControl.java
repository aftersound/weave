package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.NamedType;

public class FlagFileBasedFileFilterControl implements FileFilterControl {

    static final NamedType<FileFilterControl> TYPE = NamedType.of(
            "FlagFile",
            FlagFileBasedFileFilterControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String flagFileExtensionName;
    private String flagFileNamePattern;

    public String getFlagFileExtensionName() {
        return flagFileExtensionName;
    }

    public void setFlagFileExtensionName(String flagFileExtensionName) {
        this.flagFileExtensionName = flagFileExtensionName;
    }

    public String getFlagFileNamePattern() {
        return flagFileNamePattern;
    }

    public void setFlagFileNamePattern(String flagFileNamePattern) {
        this.flagFileNamePattern = flagFileNamePattern;
    }

}
