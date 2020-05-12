package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.CoreParameterProcessor;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.Validator;

import javax.servlet.http.HttpServletRequest;

public class RuntimeConfigImpl implements RuntimeConfig {

    private final WeaveServiceProperties properties;

    public RuntimeConfigImpl(WeaveServiceProperties properties) {
        this.properties = properties;
    }

    @Override
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ActorBindingsConfigProvider(properties);
    }

    @Override
    public ConfigProvider<Endpoint> getClientConfigProvider() {
        return new DirectoryBasedConfigProvider<>(properties.getClientConfigDirectory(), Endpoint.class);
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new DirectoryBasedConfigProvider<>(properties.getServiceMetadataDirectory(), ServiceMetadata.class);
    }

    @Override
    public ConfigProvider<ResourceConfig> getResourceConfigProvider() {
        return new DirectoryBasedConfigProvider<>(properties.getResourceConfigDirectory(), ResourceConfig.class);
    }

    @Override
    public ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider() {
        return new DirectoryBasedConfigProvider<>(properties.getAdminServiceMetadataDirectory(), ServiceMetadata.class);
    }

    @Override
    public ConfigProvider<ResourceConfig> getAdminResourceConfigProvider() {
        return new DirectoryBasedConfigProvider<>(properties.getResourceConfigDirectory(), ResourceConfig.class);
    }

    @Override
    public ParameterProcessor<HttpServletRequest> getParameterProcessor(
            ActorRegistry<Validator> paramValidatorRegistry,
            ActorRegistry<Deriver> paramDeriverRegistry) {
        return new CoreParameterProcessor(paramValidatorRegistry, paramDeriverRegistry);
    }

}
