package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.security.Authenticator;
import io.aftersound.weave.service.security.Authorizer;
import io.aftersound.weave.service.security.SecurityControlRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableMBeanExport
public class WeaveServiceAppConfiguration {

    private final WeaveServiceProperties properties;

    private RuntimeComponents components;

    public WeaveServiceAppConfiguration(WeaveServiceProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    protected void initialize() throws Exception {
        RuntimeConfig runtimeConfig = new RuntimeConfigImpl(properties);
        this.components = new RuntimeWeaver(false).initAndWeave(runtimeConfig);
    }

    @Bean
    protected ParameterProcessor<HttpServletRequest> parameterProcessor() {
        return components.parameterProcessor;
    }

    @Bean
    protected CacheRegistry cacheRegistry() {
        return components.cacheRegistry;
    }

    @Bean
    protected ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry() {
        return components.cacheKeyGeneratorRegistry;
    }

    @Bean
    protected SecurityControlRegistry securityControlRegistry() {
        return components.securityControlRegistry;
    }

    @Bean
    protected ActorRegistry<Authenticator> authenticatorRegistry() {
        return components.authenticatorRegistry;
    }

    @Bean
    protected ActorRegistry<Authorizer> authorizerRegistry() {
        return components.authorizerRegistry;
    }

    @Bean
    @Qualifier("adminServiceMetadataManager")
    protected ServiceMetadataManager adminServiceMetadataManager() {
        return components.adminServiceMetadataManager;
    }

    @Bean
    @Qualifier("adminServiceExecutorFactory")
    protected ServiceExecutorFactory adminServiceExecutorFactory() {
        return components.adminServiceExecutorFactory;
    }

    @Bean
    @Qualifier("serviceMetadataManager")
    protected ServiceMetadataManager serviceMetadataManager() {
        return components.serviceMetadataManager;
    }

    @Bean
    @Qualifier("serviceExecutorFactory")
    protected ServiceExecutorFactory serviceExecutorFactory() {
        return components.serviceExecutorFactory;
    }

}
