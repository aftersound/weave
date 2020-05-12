package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.Validator;

import javax.servlet.http.HttpServletRequest;

public interface RuntimeConfig {

    ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider();

    ConfigProvider<Endpoint> getClientConfigProvider();

    ConfigProvider<ServiceMetadata> getServiceMetadataProvider();

    ConfigProvider<ResourceConfig> getResourceConfigProvider();

    ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider();

    ConfigProvider<ResourceConfig> getAdminResourceConfigProvider();

    ParameterProcessor<HttpServletRequest> getParameterProcessor(
            ActorRegistry<Validator> paramValidatorRegistry,
            ActorRegistry<Deriver> paramDeriverRegistry);

}
