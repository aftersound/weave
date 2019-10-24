package io.aftersound.weave.swift;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.ReturnInfos;
import io.aftersound.weave.filehandler.FileReturnInfoKeys;

public class SwiftReturnInfoKeys extends FileReturnInfoKeys {

    public static final Key<ReturnInfos> STORAGE_OBJECTS = Key.as("storageObjects", ReturnInfos.class);

    public static final Key<String> SO_NAME = Key.as("so.name", String.class);
    public static final Key<String> SO_PATH = Key.as("so.path", String.class);
    public static final Key<String> SO_URL = Key.as("so.url", String.class);
    public static final Key<String> SO_PUBLIC_URL = Key.as("so.publicURL", String.class);
    public static final Key<String> SO_PRIVATE_URL = Key.as("so.privateURL", String.class);
    public static final Key<String> SO_CONTENT_TYPE = Key.as("so.contentType", String.class);


    public static final Key<String> CONTAINER_NAME = Key.as("container.name", String.class);
    public static final Key<String> CONTAINER_PATH = Key.as("container.path", String.class);
    public static final Key<Long> CONTAINER_BYTES_USED = Key.as("container.bytesUsed", Long.class);
    public static final Key<Integer> CONTAINER_OBJECT_COUNT = Key.as("container.objectCount", Integer.class);

}
