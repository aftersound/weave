package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.NamedType;

import java.util.regex.Pattern;

public class FlagFileBasedFileFilter extends FileFilter<FlagFileBasedFileFilterControl> {

    public static final NamedType<FileFilterControl> COMPANION_CONTROL_TYPE = FlagFileBasedFileFilterControl.TYPE;

    private final Pattern pattern;

    public FlagFileBasedFileFilter(FlagFileBasedFileFilterControl filterControl) {
        super(filterControl);

        this.pattern = Pattern.compile(filterControl.getFlagFileNamePattern());
    }

    @Override
    public boolean accept(String candidate) {
        return pattern.matcher(candidate).matches();
    }
}
