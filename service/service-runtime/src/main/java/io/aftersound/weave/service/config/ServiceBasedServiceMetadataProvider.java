package io.aftersound.weave.service.config;

import io.aftersound.weave.jersey.ClientHandle;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.runtime.ClientAndApplicationAwareConfigProvider;
import io.aftersound.weave.service.runtime.ConfigFormat;
import io.aftersound.weave.utils.MapBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

final class ServiceBasedServiceMetadataProvider extends ClientAndApplicationAwareConfigProvider<ClientHandle, ServiceMetadata> {

    ServiceBasedServiceMetadataProvider(
            ClientHandle client,
            String namespace,
            String application,
            String configIdentifier,
            ConfigFormat configFormat) {
        super(client, namespace, application, configIdentifier, configFormat);
    }

    @Override
    protected List<ServiceMetadata> getConfigList() {
        // GET /service/{namespace}/application/{name}/config/{configIdentifier}
        try {
            WebTarget target = client.webTarget().resolveTemplates(
                    MapBuilder.hashMap()
                            .kv("namespace", namespace)
                            .kv("name", application)
                            .kv("configIdentifier", configIdentifier)
                            .build()
            );

            Response response = target
                    .request(configFormat == ConfigFormat.Yaml ? "application/yaml" : MediaType.APPLICATION_JSON)
                    .buildGet()
                    .invoke();

            if (response.getStatus() == 200) {
                String content = response.readEntity(String.class);
                return Arrays.asList(configReader.readValue(content, ServiceMetadata[].class));
            } else {
                throw new Exception(
                        String.format(
                                "GET /service/%s/application/%s/config/%s response status %d",
                                namespace,
                                application,
                                configIdentifier,
                                response.getStatus()
                        )
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format(
                            "Failed to GET config at /service/%s/application/%s/config/%s",
                            namespace,
                            application,
                            configIdentifier
                    ),
                    e
            );
        }
    }
}
