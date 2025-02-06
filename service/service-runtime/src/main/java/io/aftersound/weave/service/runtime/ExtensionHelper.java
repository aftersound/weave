package io.aftersound.weave.service.runtime;

import com.google.common.cache.Cache;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.Handle;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.rl.RateLimitControl;
import io.aftersound.weave.service.rl.RateLimitDecision;
import io.aftersound.weave.service.security.Auth;
import io.aftersound.weave.service.security.AuthControl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ExtensionHelper {

    private static final boolean DO_NOT_TOLERATE_EXCEPTION = false;

    static ActorBindingsSet loadAndInitActorBindings(List<ActorBindingsConfig> abcList) throws Exception {
        Map<String, ActorBindingsConfig> abcByGroup = abcList
                .stream()
                .collect(Collectors.toMap(ActorBindingsConfig::getGroup, abc -> abc));

        ActorBindingsSet abs = new ActorBindingsSet();

        ActorBindingsConfig abc;
        List<String> types;

        // { ComponentConfig, ComponentFactory, Object } - required
        abs.componentFactoryBindings = ActorBindingsUtil.loadActorBindings(
                abcByGroup.get("COMPONENT_FACTORY").getTypes(),
                ComponentConfig.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { CacheControl, CacheFactory, Cache } - optional
        abc = abcByGroup.get("SERVICE_CACHE_FACTORY");
        types = abc != null && abc.getTypes() != null ? abc.getTypes() : Collections.emptyList();
        abs.cacheFactoryBindings = ActorBindingsUtil.loadActorBindings(
                types,
                CacheControl.class,
                Cache.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { KeyControl, KeyGenerator, Object } - optional
        abc = abcByGroup.get("SERVICE_CACHE_KEY_GENERATOR");
        types = abc != null && abc.getTypes() != null ? abc.getTypes() : Collections.emptyList();
        abs.cacheKeyGeneratorBindings = ActorBindingsUtil.loadActorBindings(
                types,
                KeyControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { AuthControl, AuthHandler, Auth } - optional
        abc = abcByGroup.get("AUTH_HANDLER");
        types = abc != null && abc.getTypes() != null ? abc.getTypes() : Collections.emptyList();
        abs.authHandlerBindings = ActorBindingsUtil.loadActorBindings(
                types,
                AuthControl.class,
                Auth.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { RateLimitControl, RateLimitEvaluator, RateLimitDecision } - optional
        abc = abcByGroup.get("RATE_LIMIT_EVALUATOR");
        types = abc != null && abc.getTypes() != null ? abc.getTypes() : Collections.emptyList();
        abs.rateLimitEvaluatorBindings = ActorBindingsUtil.loadActorBindings(
                types,
                RateLimitControl.class,
                RateLimitDecision.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // { ExecutionControl, ServiceExecutor, Object } for non-admin related service - required
        abs.serviceExecutorBindings = ActorBindingsUtil.loadActorBindings(
                abcByGroup.get("SERVICE_EXECUTOR").getTypes(),
                ExecutionControl.class,
                Object.class,
                DO_NOT_TOLERATE_EXCEPTION
        );

        // initialize MasterFuncFactory
        abc = abcByGroup.get("VALUE_FUNC_FACTORY");
        types = abc != null && abc.getTypes() != null ? abc.getTypes() : Collections.emptyList();
        MasterFuncFactory funcFactory = MasterFuncFactory.of(types.toArray(new String[0]));
        Handle.of("VALUE_FUNC_FACTORY", MasterFuncFactory.class).setAndLock(funcFactory);
        abs.funcFactory = funcFactory;

        return abs;
    }
}
