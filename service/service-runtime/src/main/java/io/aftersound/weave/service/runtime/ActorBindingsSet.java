package io.aftersound.weave.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheFactory;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.Validation;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.security.Auth;
import io.aftersound.weave.service.security.AuthControl;
import io.aftersound.weave.service.security.AuthHandler;

class ActorBindingsSet {
    ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings;
    ActorBindings<Validation, Validator, Messages> validatorBindings;
    ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;
    ActorBindings<KeyControl, KeyGenerator, Object> cacheKeyGeneratorBindings;
    ActorBindings<AuthControl, AuthHandler, Auth> authHandlerBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> adminServiceExecutorBindings;
}
