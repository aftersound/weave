package io.aftersound.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.actor.ActorBindings;
import io.aftersound.component.ComponentConfig;
import io.aftersound.component.ComponentFactory;
import io.aftersound.service.ServiceExecutor;
import io.aftersound.service.cache.CacheControl;
import io.aftersound.service.cache.CacheFactory;
import io.aftersound.service.cache.KeyControl;
import io.aftersound.service.cache.KeyGenerator;
import io.aftersound.service.metadata.ExecutionControl;
import io.aftersound.service.rl.RateLimitControl;
import io.aftersound.service.rl.RateLimitDecision;
import io.aftersound.service.rl.RateLimitEvaluator;
import io.aftersound.service.security.Auth;
import io.aftersound.service.security.AuthControl;
import io.aftersound.service.security.AuthHandler;

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
