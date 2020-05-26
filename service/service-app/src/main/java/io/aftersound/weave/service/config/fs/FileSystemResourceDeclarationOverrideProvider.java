package io.aftersound.weave.service.config.fs;

import io.aftersound.weave.client.fs.FileSystem;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;
import io.aftersound.weave.service.runtime.ResourceDeclarationOverride;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class FileSystemResourceDeclarationOverrideProvider
        extends ClientAndNamespaceAwareConfigProvider<FileSystem, ResourceDeclarationOverride> {

    FileSystemResourceDeclarationOverrideProvider(
            FileSystem client,
            String namespace,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, configIdentifier, configFormat);
    }

    @Override
    protected List<ResourceDeclarationOverride> getConfigList() {
        String fileName = Util.getFileName(configIdentifier, configFormat);
        try (InputStream is = client.getAsStream(namespace, fileName)) {
            return Arrays.asList(configReader.readValue(is, ResourceDeclarationOverride[].class));
        } catch (Exception e) {
            // ${namespace}/${configIdentifier}.json
            throw new RuntimeException("failed to read config from " + fileName, e);
        }
    }
}
