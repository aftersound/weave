package io.aftersound.weave.batch;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.filehandler.FileHandlerFactory;

public final class ResourceTypes {

    public static final NamedType<ComponentRegistry> COMPONENT_REGISTRY = NamedType.of(
            ComponentRegistry.class.getName(),
            ComponentRegistry.class
    );

    public static final NamedType<FileHandlerFactory> FILE_HANDLER_FACTORY = NamedType.of(
            FileHandlerFactory.class.getName(),
            FileHandlerFactory.class
    );

    public static final NamedType<String> JOB_DATA_DIR = NamedType.of("JOB_DATA_DIR", String.class);
}
