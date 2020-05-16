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

class RuntimeComponentsImpl implements RuntimeComponents {

    // admin service specific
    private ServiceMetadataRegistry adminServiceMetadataRegistry;
    private ServiceExecutorFactory adminServiceExecutorFactory;

    // non-admin service specific
    private ServiceMetadataRegistry serviceMetadataRegistry;
    private ServiceExecutorFactory serviceExecutorFactory;

    // common across admin and non-admin services
    // authentication and authorization related
    private SecurityControlRegistry securityControlRegistry;
    private ActorRegistry<Authenticator> authenticatorRegistry;
    private ActorRegistry<Authorizer> authorizerRegistry;

    // parameter processing related
    private ParameterProcessor<HttpServletRequest> parameterProcessor;

    // cache related
    private CacheRegistry cacheRegistry;
    private ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;

    private ManagementFacades managementFacades;

    void setAdminServiceMetadataRegistry(ServiceMetadataRegistry adminServiceMetadataRegistry) {
        this.adminServiceMetadataRegistry = adminServiceMetadataRegistry;
    }

    void setAdminServiceExecutorFactory(ServiceExecutorFactory adminServiceExecutorFactory) {
        this.adminServiceExecutorFactory = adminServiceExecutorFactory;
    }

    void setServiceMetadataRegistry(ServiceMetadataRegistry serviceMetadataRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
    }

    void setServiceExecutorFactory(ServiceExecutorFactory serviceExecutorFactory) {
        this.serviceExecutorFactory = serviceExecutorFactory;
    }

    void setSecurityControlRegistry(SecurityControlRegistry securityControlRegistry) {
        this.securityControlRegistry = securityControlRegistry;
    }

    void setAuthenticatorRegistry(ActorRegistry<Authenticator> authenticatorRegistry) {
        this.authenticatorRegistry = authenticatorRegistry;
    }

    void setAuthorizerRegistry(ActorRegistry<Authorizer> authorizerRegistry) {
        this.authorizerRegistry = authorizerRegistry;
    }

    void setParameterProcessor(ParameterProcessor<HttpServletRequest> parameterProcessor) {
        this.parameterProcessor = parameterProcessor;
    }

    void setCacheRegistry(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    void setCacheKeyGeneratorRegistry(ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry) {
        this.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;
    }

    void setManagementFacades(ManagementFacades managementFacades) {
        this.managementFacades = managementFacades;
    }

    @Override
    public ServiceMetadataRegistry adminServiceMetadataRegistry() {
        return adminServiceMetadataRegistry;
    }

    @Override
    public ServiceExecutorFactory adminServiceExecutorFactory() {
        return adminServiceExecutorFactory;
    }

    @Override
    public ServiceMetadataRegistry serviceMetadataRegistry() {
        return serviceMetadataRegistry;
    }

    @Override
    public ServiceExecutorFactory serviceExecutorFactory() {
        return serviceExecutorFactory;
    }

    @Override
    public SecurityControlRegistry securityControlRegistry() {
        return securityControlRegistry;
    }

    @Override
    public ActorRegistry<Authenticator> authenticatorRegistry() {
        return authenticatorRegistry;
    }

    @Override
    public ActorRegistry<Authorizer> authorizerRegistry() {
        return authorizerRegistry;
    }

    @Override
    public ParameterProcessor<HttpServletRequest> parameterProcessor() {
        return parameterProcessor;
    }

    @Override
    public CacheRegistry cacheRegistry() {
        return cacheRegistry;
    }

    @Override
    public ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry() {
        return cacheKeyGeneratorRegistry;
    }

    @Override
    public ManagementFacades managementFacades() {
        return managementFacades;
    }
}
