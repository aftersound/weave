package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.cache.KeyGenerator;
import io.aftersound.weave.security.Authenticator;
import io.aftersound.weave.security.Authorizer;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.Validator;
import io.aftersound.weave.service.security.SecurityControlRegistry;

import javax.servlet.http.HttpServletRequest;

class ComponentBag {

    // admin service specific
    AdminServiceMetadataManager adminServiceMetadataManager;
    ServiceExecutorFactory adminServiceExecutorFactory;

    // non-admin service specific
    WeaveServiceMetadataManager serviceMetadataManager;
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
