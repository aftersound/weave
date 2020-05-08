package io.aftersound.weave.batch;

import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.resource.ResourceType;

public final class ResourceTypes {

    public static final ResourceType<ClientRegistry> CLIENT_REGISTRY = new ResourceType<>(
            ClientRegistry.class.getName(),
            ClientRegistry.class
    );

    public static final ResourceType<FileHandlerFactory> FILE_HANDLER_FACTORY = new ResourceType<>(
            FileHandlerFactory.class.getName(),
            FileHandlerFactory.class
    );

    public static final ResourceType<String> JOB_DATA_DIR = new ResourceType<>("JOB_DATA_DIR", String.class);
}
