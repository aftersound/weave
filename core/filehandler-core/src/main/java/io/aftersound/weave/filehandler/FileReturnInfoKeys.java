package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.Key;

import java.util.List;

public class FileReturnInfoKeys {

    public static final Key<String> SOURCE_FILE_PATH = Key.of("sourceFilePath");
    public static final Key<String> TARGET_FILE_PATH = Key.of("targetFilePath");
    public static final Key<List> FILE_LIST = Key.of("fileList");
}
