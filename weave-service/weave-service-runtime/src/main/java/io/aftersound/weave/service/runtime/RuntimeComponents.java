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

    ServiceMetadataRegistry adminServiceMetadataRegistry();
    ServiceExecutorFactory adminServiceExecutorFactory();

    ServiceMetadataRegistry serviceMetadataRegistry();
    ServiceExecutorFactory serviceExecutorFactory();

    SecurityControlRegistry securityControlRegistry();
    ActorRegistry<Authenticator> authenticatorRegistry();
    ActorRegistry<Authorizer> authorizerRegistry();

    ParameterProcessor<HttpServletRequest> parameterProcessor();

    CacheRegistry cacheRegistry();
    ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry();

    ManagementFacades managementFacades();
}
