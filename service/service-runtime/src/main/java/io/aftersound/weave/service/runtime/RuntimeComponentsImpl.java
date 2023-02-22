package io.aftersound.weave.service.runtime;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.rl.RateLimitControlRegistry;
import io.aftersound.weave.service.rl.RateLimitEvaluator;
import io.aftersound.weave.service.security.AuthHandler;
import io.aftersound.weave.service.security.AuthControlRegistry;

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
    private AuthControlRegistry authControlRegistry;
    private ActorRegistry<AuthHandler> authenticatorRegistry;

    // parameter processing related
    private ParameterProcessor<HttpServletRequest> parameterProcessor;

    // cache related
    private CacheRegistry cacheRegistry;
    private ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;

    // rate limit related
    private RateLimitControlRegistry rateLimitControlRegistry;
    private ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry;

    private Initializer initializer;

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

    void setAuthControlRegistry(AuthControlRegistry authControlRegistry) {
        this.authControlRegistry = authControlRegistry;
    }

    void setAuthHandlerRegistry(ActorRegistry<AuthHandler> authenticatorRegistry) {
        this.authenticatorRegistry = authenticatorRegistry;
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

    void setRateLimitControlRegistry(RateLimitControlRegistry rateLimitControlRegistry) {
        this.rateLimitControlRegistry = rateLimitControlRegistry;
    }

    void setRateLimitEvaluatorRegistry(ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry) {
        this.rateLimitEvaluatorRegistry = rateLimitEvaluatorRegistry;
    }

    void setInitializer(Initializer initializer) {
        this.initializer = initializer;
    }

    void setManagementFacades(ManagementFacades managementFacades) {
        this.managementFacades = managementFacades;
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
    public AuthControlRegistry authControlRegistry() {
        return authControlRegistry;
    }

    @Override
    public ActorRegistry<AuthHandler> authHandlerRegistry() {
        return authenticatorRegistry;
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
    public RateLimitControlRegistry rateLimitControlRegistry() {
        return rateLimitControlRegistry;
    }

    @Override
    public ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry() {
        return rateLimitEvaluatorRegistry;
    }

    @Override
    public Initializer initializer() {
        return initializer;
    }

    @Override
    public ManagementFacades managementFacades() {
        return managementFacades;
    }
}
