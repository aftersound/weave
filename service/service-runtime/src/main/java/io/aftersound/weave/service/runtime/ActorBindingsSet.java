package io.aftersound.weave.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.func.FuncFactory;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheFactory;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.rl.RateLimitControl;
import io.aftersound.weave.service.rl.RateLimitDecision;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.security.Auth;
import io.aftersound.weave.service.security.AuthControl;
import io.aftersound.weave.service.security.AuthHandler;

import java.util.List;

@SuppressWarnings("rawtypes")
class ActorBindingsSet {
    ActorBindings<ComponentConfig, ComponentFactory<?>, Object> componentFactoryBindings;
    ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;
    ActorBindings<KeyControl, KeyGenerator, Object> cacheKeyGeneratorBindings;
    ActorBindings<AuthControl, AuthHandler, Auth> authHandlerBindings;
    ActorBindings<RateLimitControl, RateLimitEvaluator, RateLimitDecision> rateLimitEvaluatorBindings;
    ActorBindings<ExecutionControl, ServiceExecutor, Object> serviceExecutorBindings;
    List<String> masterAwareFuncFactoryClasses;
}
