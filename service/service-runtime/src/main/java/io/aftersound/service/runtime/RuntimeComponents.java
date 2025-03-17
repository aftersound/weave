package io.aftersound.service.runtime;

import io.aftersound.actor.ActorRegistry;
import io.aftersound.service.ServiceExecutor;
import io.aftersound.service.metadata.ServiceMetadata;
import io.aftersound.service.rl.RateLimitControl;
import io.aftersound.service.security.AuthControl;
import io.aftersound.service.ServiceMetadataRegistry;
import io.aftersound.service.cache.CacheRegistry;
import io.aftersound.service.cache.KeyGenerator;
import io.aftersound.service.request.ParameterProcessor;
import io.aftersound.service.rl.RateLimitControlRegistry;
import io.aftersound.service.rl.RateLimitEvaluator;
import io.aftersound.service.security.AuthControlRegistry;
import io.aftersound.service.security.AuthHandler;

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
     *          a registry of {@link ServiceMetadata}
     *          for non-administration purpose
     */
    ServiceMetadataRegistry serviceMetadataRegistry();

    /**
     * @return
     *          a factory which creates {@link ServiceExecutor}
     *          for non-administration purpose and provides access to them
     */
    ServiceExecutorFactory serviceExecutorFactory();

    /**
     * @return
     *          a registry which holds {@link AuthControl}s
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
     *          a registry which holds {@link RateLimitControl}s
     *          and provides access to them
     */
    RateLimitControlRegistry rateLimitControlRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} of {@link RateLimitEvaluator}
     */
    ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry();

}
