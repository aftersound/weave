package io.aftersound.weave.batch;

import io.aftersound.weave.filehandler.FileHandlingControl;

public interface WithSourceFileHandlingControl {
    FileHandlingControl getSourceFileHandlingControl();
    void setSourceFileHandlingControl(FileHandlingControl sourceFileHandlingControl);
}
