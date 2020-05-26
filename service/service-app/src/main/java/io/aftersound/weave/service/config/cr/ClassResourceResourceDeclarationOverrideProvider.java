package io.aftersound.weave.service.config.cr;

import io.aftersound.weave.client.cr.ClassResource;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;
import io.aftersound.weave.service.runtime.ResourceDeclarationOverride;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class ClassResourceResourceDeclarationOverrideProvider
        extends ClientAndNamespaceAwareConfigProvider<ClassResource, ResourceDeclarationOverride> {

    ClassResourceResourceDeclarationOverrideProvider(
            ClassResource client,
            String namespace,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, configIdentifier, configFormat);
    }

    @Override
    protected List<ResourceDeclarationOverride> getConfigList() {
        String resourceName = Util.getResourceName(namespace, configIdentifier, configFormat);
        try (InputStream is = client.getAsStream(resourceName)) {
            return Arrays.asList(configReader.readValue(is, ResourceDeclarationOverride[].class));
        } catch (Exception e) {
            // ${namespace}/${configIdentifier}.json
            throw new RuntimeException("failed to read config from " + resourceName, e);
        }
    }
}
