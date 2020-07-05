package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.service.request.CoreParameterProcessor;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareRuntimeConfig;
import io.aftersound.weave.service.runtime.ConfigFormat;
import io.aftersound.weave.service.runtime.ConfigUpdateStrategy;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseRuntimeConfig<CLIENT> extends ClientAndNamespaceAwareRuntimeConfig<CLIENT> {

    protected BaseRuntimeConfig(
            ClientRegistry clientRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        super(clientRegistry, clientId, namespace, configFormat, configUpdateStrategy);
    }

    @Override
    public final ParameterProcessor<HttpServletRequest> getParameterProcessor(
            ActorRegistry<CodecFactory> codecFactoryRegistry,
            ActorRegistry<Validator> paramValidatorRegistry,
            ActorRegistry<Deriver> paramDeriverRegistry) {
        return new CoreParameterProcessor(codecFactoryRegistry, paramValidatorRegistry, paramDeriverRegistry);
    }
}
