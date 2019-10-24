package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.NamedType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileListBasedFileFilterControl implements FileFilterControl {

    static final NamedType<FileFilterControl> TYPE = NamedType.of(
            "FileList",
            FileListBasedFileFilterControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private List<String> filePaths;

    private transient Set<String> filePathSet;

    public List<String> getFilePaths() {
        return filePaths;
    }

    public void setFilePaths(List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public Set<String> filePathSet() {
        if (filePathSet == null) {
            filePathSet = filePaths != null ? new HashSet<String>(filePaths) : Collections.<String>emptySet();
        }
        return filePathSet;
    }

}
