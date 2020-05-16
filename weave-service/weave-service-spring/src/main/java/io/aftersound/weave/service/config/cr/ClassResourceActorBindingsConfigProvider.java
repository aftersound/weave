package io.aftersound.weave.service.config.cr;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.client.cr.ClassResource;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

final class ClassResourceActorBindingsConfigProvider extends ClientAndNamespaceAwareConfigProvider<ClassResource, ActorBindingsConfig> {

    ClassResourceActorBindingsConfigProvider(ClassResource client, String namespace, String configIdentifier, ConfigFormat configFormat) {
        super(client, namespace, configIdentifier, configFormat);
    }

    @Override
    protected List<ActorBindingsConfig> getConfigList() {
        String resourceName = Util.getResourceName(namespace, configIdentifier, configFormat);
        try (InputStream is = client.getAsStream(resourceName)) {
            return Arrays.asList(configReader.readValue(is, ActorBindingsConfig[].class));
        } catch (Exception e) {
            // ${namespace}/${configIdentifier}.json
            throw new RuntimeException("failed to read config from " + resourceName, e);
        }
    }
}
