package io.aftersound.weave.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatControl;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.resource.ResourceManager;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheFactory;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.Validation;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.security.*;

class ActorBindingsSet {
    ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;
    ActorBindings<KeyControl, KeyGenerator, Object> cacheKeyGeneratorBindings;
    ActorBindings<Endpoint, ClientFactory<?>, Object> clientFactoryBindings;
    ActorBindings<Validation, Validator, Messages> validatorBindings;
    ActorBindings<DeriveControl, Deriver, ParamValueHolder> deriverBindings;
    ActorBindings<DataFormatControl, DataFormat, Object> dataFormatBindings;
    ActorBindings<AuthenticationControl, Authenticator, Authentication> authenticatorBindings;
    ActorBindings<AuthorizationControl, Authorizer, Authorization> authorizerBindings;
    ActorBindings<ResourceConfig, ResourceManager, Object> resourceManagerBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings;
    ActorBindings<ResourceConfig, ResourceManager, Object> adminResourceManagerBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> adminServiceExecutorBindings;
}
