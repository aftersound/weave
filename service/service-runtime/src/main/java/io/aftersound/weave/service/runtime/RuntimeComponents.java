package io.aftersound.weave.service.runtime;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.security.Authenticator;
import io.aftersound.weave.service.security.Authorizer;
import io.aftersound.weave.service.security.SecurityControlRegistry;

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
     *          for administration purpose and provides access to them
     */
    ServiceMetadataRegistry adminServiceMetadataRegistry();

    /**
     * @return
     *          a factory which creates {@link io.aftersound.weave.service.ServiceExecutor}
     *          for administration purpose and provides access to them
     */
    ServiceExecutorFactory adminServiceExecutorFactory();

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
     *          a registry which holds {@link io.aftersound.weave.service.security.SecurityControl}s
     *          and provides access to them
     */
    SecurityControlRegistry securityControlRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} for {@link Authenticator}s
     */
    ActorRegistry<Authenticator> authenticatorRegistry();

    /**
     * @return
     *          an {@link ActorRegistry} for {@link Authorizer}s
     */
    ActorRegistry<Authorizer> authorizerRegistry();

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

}
