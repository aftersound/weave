package io.aftersound.weave.service.runtime;

import io.aftersound.actor.ActorRegistry;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.rl.RateLimitControlRegistry;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.security.AuthControlRegistry;
import io.aftersound.weave.service.security.AuthHandler;

import javax.servlet.http.HttpServletRequest;

public interface RuntimeComponents {

    /**
     * @return
     *          {@link Initializer} which initializes instances of installed extensions
     */
    Initializer initializer();

    /**
     * @return
     *          a collection of {@link ManagementFacade}s
     */
    ManagementFacades managementFacades();

    /**
     * @return
     *          a registry of {@link io.aftersound.weave.service.metadata.ServiceMetadata}
     *          for non-administration purpose
     */
    ServiceMetadataRegistry serviceMetadataRegistry();

    /**
     * @return
     *          a factory which creates {@link io.aftersound.weave.service.ServiceExecutor}
     *          for non-administration purpose and provides access to them
     */
    ServiceExecutorFactory serviceExecutorFactory();

    /**
     * @return
     *          a registry which holds {@link io.aftersound.weave.service.security.AuthControl}s
     *          and provides access to them
     */
    AuthControlRegistry authControlRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} for {@link AuthHandler}s
     */
    ActorRegistry<AuthHandler> authHandlerRegistry();

    /**
     * @return
     *          an {@link ParameterProcessor} for {@link HttpServletRequest}
     */
    ParameterProcessor<HttpServletRequest> parameterProcessor();

    /**
     * @return
     *          a registry for service response caches
     */
    CacheRegistry cacheRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} of service response cache {@link KeyGenerator}
     */
    ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry();

    /**
     * @return
     *          a registry which holds {@link io.aftersound.weave.service.rl.RateLimitControl}s
     *          and provides access to them
     */
    RateLimitControlRegistry rateLimitControlRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} of {@link RateLimitEvaluator}
     */
    ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry();

}
