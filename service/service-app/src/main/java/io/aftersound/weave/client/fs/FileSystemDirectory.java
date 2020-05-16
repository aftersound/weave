package io.aftersound.weave.client.fs;

import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.common.NamedType;

public class FileSystemDirectory extends ClientFactory<FileSystem> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("FileSystem", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("FileSystem", FileSystem.class);

    public FileSystemDirectory(ClientRegistry clientRegistry) {
        super(clientRegistry);
    }

    @Override
    protected FileSystem createClient(Endpoint endpoint) {
        return null;
    }

    @Override
    protected void destroyClient(FileSystem fileSystem) {

    }
}
