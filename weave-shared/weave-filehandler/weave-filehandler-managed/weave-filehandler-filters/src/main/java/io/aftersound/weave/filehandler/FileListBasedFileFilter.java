package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.NamedType;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileListBasedFileFilter extends FileFilter<FileListBasedFileFilterControl> {

    public static final NamedType<FileFilterControl> COMPANION_CONTROL_TYPE = FileListBasedFileFilterControl.TYPE;

    private final Set<String> filePathSet;

    public FileListBasedFileFilter(FileListBasedFileFilterControl filterControl) {
        super(filterControl);

        List<String> filePaths = filterControl.getFilePaths();
        Set<String> filePathSet = filePaths != null ? new HashSet<String>(filePaths) : Collections.<String>emptySet();
        this.filePathSet = filePathSet;
    }

    @Override
    public boolean accept(String candidate) {
        return filePathSet.contains(candidate);
    }
}
