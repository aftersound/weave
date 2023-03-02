package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.fs.FileSystem;
import io.aftersound.weave.fs.Reader;
import io.aftersound.weave.service.runtime.ClientAndApplicationAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class FSBasedActorBindingsConfigProvider extends ClientAndApplicationAwareConfigProvider<FileSystem, ActorBindingsConfig> {

    FSBasedActorBindingsConfigProvider(
            FileSystem client,
            String namespace,
            String application,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, application, configIdentifier, configFormat);
    }

    @Override
    protected List<ActorBindingsConfig> getConfigList() {
        String directory = String.format("%s/%s", namespace, application);
        String fileName = FSBasedUtil.getFileName(configIdentifier, configFormat);

        Reader<ActorBindingsConfig[]> reader = new Reader<ActorBindingsConfig[]>() {

            @Override
            protected ActorBindingsConfig[] read(InputStream is) throws Exception {
                return configReader.readValue(is, ActorBindingsConfig[].class);
            }

        };

        try {
            return Arrays.asList(client.fileSystem(directory).read(fileName, reader));
        } catch (Exception e) {
            // ${configIdentifier}.${configFormat}
            throw new RuntimeException("failed to read config from " + fileName, e);
        }
    }
}
