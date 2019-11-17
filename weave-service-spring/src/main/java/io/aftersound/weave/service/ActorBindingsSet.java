package io.aftersound.weave.service;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheFactory;
import io.aftersound.weave.cache.KeyControl;
import io.aftersound.weave.cache.KeyGenerator;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.data.DataFormatControl;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.security.Authentication;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import io.aftersound.weave.security.Authorization;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.security.Authorizer;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;

class ActorBindingsSet {
    ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;
    ActorBindings<KeyControl, KeyGenerator, Object> cacheKeyGeneratorBindings;
    ActorBindings<Endpoint, DataClientFactory<?>, Object>  dataClientFactoryBindings;
    ActorBindings<DeriveControl, Deriver, ParamValueHolder> deriverBindings;
    ActorBindings<DataFormatControl, DataFormat, Object> dataFormatBindings;
    ActorBindings<AuthenticationControl, Authenticator, Authentication> authenticatorBindings;
    ActorBindings<AuthorizationControl, Authorizer, Authorization> authorizerBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> adminServiceExecutorBindings;
}
