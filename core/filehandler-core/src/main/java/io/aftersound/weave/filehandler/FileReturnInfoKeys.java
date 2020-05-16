package io.aftersound.weave.filehandler;

import io.aftersound.weave.common.Key;

import java.util.List;

public class FileReturnInfoKeys {

    public static final Key<String> SOURCE_FILE_PATH = Key.as("sourceFilePath", String.class);
    public static final Key<String> TARGET_FILE_PATH = Key.as("targetFilePath", String.class);
    public static final Key<List> FILE_LIST = Key.as("fileList", List.class);
}
