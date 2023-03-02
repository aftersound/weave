package io.aftersound.weave.service.config;

import io.aftersound.weave.fs.FileSystem;
import io.aftersound.weave.fs.Reader;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.runtime.ClientAndApplicationAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class FSBasedServiceMetadataProvider extends ClientAndApplicationAwareConfigProvider<FileSystem, ServiceMetadata> {

    FSBasedServiceMetadataProvider(
            FileSystem client,
            String namespace,
            String application,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, application, configIdentifier, configFormat);
    }

    @Override
    protected List<ServiceMetadata> getConfigList() {
        String directory = String.format("%s/%s", namespace, application);
        String fileName = FSBasedUtil.getFileName(configIdentifier, configFormat);

        Reader<ServiceMetadata[]> reader = new Reader<ServiceMetadata[]>() {

            @Override
            protected ServiceMetadata[] read(InputStream is) throws Exception {
                return configReader.readValue(is, ServiceMetadata[].class);
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
