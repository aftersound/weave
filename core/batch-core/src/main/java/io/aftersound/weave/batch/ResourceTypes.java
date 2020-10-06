package io.aftersound.weave.batch;

import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.filehandler.FileHandlerFactory;
import io.aftersound.weave.resource.ResourceType;

public final class ResourceTypes {

    public static final ResourceType<ComponentRegistry> COMPONENT_REGISTRY = new ResourceType<>(
            ComponentRegistry.class.getName(),
            ComponentRegistry.class
    );

    public static final ResourceType<FileHandlerFactory> FILE_HANDLER_FACTORY = new ResourceType<>(
            FileHandlerFactory.class.getName(),
            FileHandlerFactory.class
    );

    public static final ResourceType<String> JOB_DATA_DIR = new ResourceType<>("JOB_DATA_DIR", String.class);
}
