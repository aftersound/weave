package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.data.DataFormatRegistry;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.security.Authorizer;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.security.SecurityControlRegistry;

import java.util.Collection;

class ComponentBag {

    // common across admin service and non-admin service
    Collection<Class<? extends Authenticator>> authenticatorTypes;
    NamedTypes<AuthenticationControl> authenticationControlTypes;
    Collection<Class<? extends Authorizer>> authorizerTypes;
    NamedTypes<AuthorizationControl> authorizationControlTypes;

    SecurityControlRegistry securityControlRegistry;

    // admin service specific
    Collection<Class<? extends ServiceExecutor>> adminServiceExecutorTypes;
    NamedTypes<ExecutionControl> adminExecutionControlTypes;
    ObjectMapper adminServiceMetadataReader;
    AdminServiceMetadataManager adminServiceMetadataManager;

    // non-admin service specific
    NamedTypes<DeriveControl> paramDeriveControlTypes;
    ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory;
    Collection<Class<? extends ServiceExecutor>> serviceExecutorTypes;
    NamedTypes<ExecutionControl> executionControlTypes;
    ObjectMapper serviceMetadataReader;
    WeaveServiceMetadataManager serviceMetadataManager;
    CacheRegistry cacheRegistry;

    // batch specific
    ObjectMapper jobSpecReader;

    // shared
    NamedTypes<CacheControl> cacheControlTypes;
    DataClientRegistry dataClientRegistry;
    DataFormatRegistry dataFormatRegistry;
}
