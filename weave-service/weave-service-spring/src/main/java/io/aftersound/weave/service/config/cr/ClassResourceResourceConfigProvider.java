package io.aftersound.weave.service.config.cr;

import io.aftersound.weave.client.cr.ClassResource;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class ClassResourceResourceConfigProvider extends ClientAndNamespaceAwareConfigProvider<ClassResource, ResourceConfig> {

    ClassResourceResourceConfigProvider(ClassResource client, String namespace, String configIdentifier, ConfigFormat configFormat) {
        super(client, namespace, configIdentifier, configFormat);
    }

    @Override
    protected List<ResourceConfig> getConfigList() {
        String resourceName = Util.getResourceName(namespace, configIdentifier, configFormat);
        try (InputStream is = client.getAsStream(resourceName)) {
            return Arrays.asList(configReader.readValue(is, ResourceConfig[].class));
        } catch (Exception e) {
            // ${namespace}/${configIdentifier}.json
            throw new RuntimeException("failed to read config from " + resourceName, e);
        }
    }
}
