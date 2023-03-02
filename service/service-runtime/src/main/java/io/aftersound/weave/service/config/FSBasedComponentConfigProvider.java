package io.aftersound.weave.service.config;

import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.fs.FileSystem;
import io.aftersound.weave.fs.Reader;
import io.aftersound.weave.service.runtime.ClientAndApplicationAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class FSBasedComponentConfigProvider extends ClientAndApplicationAwareConfigProvider<FileSystem, ComponentConfig> {

    FSBasedComponentConfigProvider(
            FileSystem client,
            String namespace,
            String application,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, application, configIdentifier, configFormat);
    }

    @Override
    protected List<ComponentConfig> getConfigList() {
        String directory = String.format("%s/%s", namespace, application);
        String fileName = FSBasedUtil.getFileName(configIdentifier, configFormat);

        Reader<ComponentConfig[]> reader = new Reader<ComponentConfig[]>() {

            @Override
            protected ComponentConfig[] read(InputStream is) throws Exception {
                return configReader.readValue(is, ComponentConfig[].class);
            }

        };

        try {
            return Arrays.asList(client.fileSystem(directory).read(fileName, reader));
        } catch (Exception e) {
            // ${configIdentifier}.${configFormat}
            throw new RuntimeException("failed to read component config from " + fileName, e);
        }
    }
}
