package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.security.AuthenticatorFactory;
import io.aftersound.weave.service.security.AuthorizerFactory;
import io.aftersound.weave.service.security.SecurityControlRegistry;

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
    AuthenticatorFactory authenticatorFactory;
    AuthorizerFactory authorizerFactory;

    // parameter derivation related
    ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory;

    // cache related
    CacheRegistry cacheRegistry;
}
