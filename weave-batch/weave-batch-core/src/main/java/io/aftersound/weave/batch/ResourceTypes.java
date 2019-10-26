package io.aftersound.weave.batch;

import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.resources.ResourceType;

public final class ResourceTypes {

    public static final ResourceType<DataClientRegistry> DATA_CLIENT_REGISTRY = new ResourceType<>(
            DataClientRegistry.class.getName(),
            DataClientRegistry.class
    );

    public static final ResourceType<FileHandlerFactory> FILE_HANDLER_FACTORY = new ResourceType<>(
            FileHandlerFactory.class.getName(),
            FileHandlerFactory.class
    );

    public static final ResourceType<String> JOB_DATA_DIR = new ResourceType<>("JOB_DATA_DIR", String.class);
}
