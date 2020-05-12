package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.security.Authenticator;
import io.aftersound.weave.service.security.Authorizer;
import io.aftersound.weave.service.security.SecurityControlRegistry;

import javax.servlet.http.HttpServletRequest;

public class RuntimeComponents {

    // admin service specific
    ServiceMetadataManager adminServiceMetadataManager;
    ServiceExecutorFactory adminServiceExecutorFactory;

    // non-admin service specific
    ServiceMetadataManager serviceMetadataManager;
    ServiceExecutorFactory serviceExecutorFactory;

    // common across admin and non-admin services
    // authentication and authorization related
    SecurityControlRegistry securityControlRegistry;
    ActorRegistry<Authenticator> authenticatorRegistry;
    ActorRegistry<Authorizer> authorizerRegistry;

    // parameter processing related
    ParameterProcessor<HttpServletRequest> parameterProcessor;

    // cache related
    CacheRegistry cacheRegistry;
    ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;
}
