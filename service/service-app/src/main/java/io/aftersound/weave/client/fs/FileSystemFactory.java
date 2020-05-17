package io.aftersound.weave.client.fs;

import io.aftersound.weave.client.ClientConfigUtils;
import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.config.Settings;

import static io.aftersound.weave.client.fs.SettingsDictionary.BASE_DIRECTORY;

public class FileSystemFactory extends ClientFactory<FileSystem> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("FileSystem", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("FileSystem", FileSystem.class);

    public FileSystemFactory(ClientRegistry clientRegistry) {
        super(clientRegistry);
    }

    @Override
    protected FileSystem createClient(Endpoint endpoint) {
        Settings settings = Settings.from(
                ClientConfigUtils.extractConfig(
                        endpoint.getOptions(),
                        SettingsDictionary.CONFIG_KEYS,
                        SettingsDictionary.SECURITY_KEYS
                )
        );

        return new FileSystem(settings.v(BASE_DIRECTORY));
    }

    @Override
    protected void destroyClient(FileSystem fileSystem) {
        // Do nothing
    }
}
