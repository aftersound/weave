package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeaveServiceProperties {

    @Value("${security-asset-directory}")
    private String securityAssetDirectory;

    @Value("${cache-factory-types-json}")
    private String cacheFactoryTypesJson;

    @Value("${cache-key-generator-types-json}")
    private String cacheKeyGeneratorTypesJson;

    @Value("${data-client-factory-types-json}")
    private String dataClientFactoryTypesJson;

    @Value("${param-validator-types-json}")
    private String paramValidatorTypesJson;

    @Value("${param-deriver-types-json}")
    private String paramDeriverTypesJson;

    @Value("${data-format-types-json}")
    private String dataFormatTypesJson;

    @Value("${resource-manager-types-json}")
    private String resourceManagerTypesJson;

    @Value("${service-executor-types-json}")
    private String serviceExecutorTypesJson;

    @Value("${authenticator-types-json}")
    private String authenticatorTypesJson;

    @Value("${authorizer-types-json}")
    private String authorizerTypesJson;

    @Value("${job-runner-types-json}")
    private String jobRunnerTypesJson;

    @Value("${admin-service-metadata-directory}")
    private String adminServiceMetadataDirectory;

    @Value("${admin-service-executor-types-json}")
    private String adminServiceExecutorTypesJson;

    @Value("${data-client-config-directory}")
    private String dataClientConfigDirectory;

    @Value("${resource-config-directory}")
    private String resourceConfigDirectory;

    @Value("${service-metadata-directory}")
    private String serviceMetadataDirectory;

    @Value("${job-spec-directory}")
    private String jobSpecDirectory;

    public String getSecurityAssetDirectory() {
        return securityAssetDirectory;
    }

    public void setSecurityAssetDirectory(String securityAssetDirectory) {
        this.securityAssetDirectory = securityAssetDirectory;
    }

    public String getCacheFactoryTypesJson() {
        return cacheFactoryTypesJson;
    }

    public void setCacheFactoryTypesJson(String cacheFactoryTypesJson) {
        this.cacheFactoryTypesJson = cacheFactoryTypesJson;
    }

    public String getCacheKeyGeneratorTypesJson() {
        return cacheKeyGeneratorTypesJson;
    }

    public void setCacheKeyGeneratorTypesJson(String cacheKeyGeneratorTypesJson) {
        this.cacheKeyGeneratorTypesJson = cacheKeyGeneratorTypesJson;
    }

    public String getDataClientFactoryTypesJson() {
        return dataClientFactoryTypesJson;
    }

    public void setDataClientFactoryTypesJson(String dataClientFactoryTypesJson) {
        this.dataClientFactoryTypesJson = dataClientFactoryTypesJson;
    }

    public String getParamValidatorTypesJson() {
        return paramValidatorTypesJson;
    }

    public void setParamValidatorTypesJson(String paramValidatorTypesJson) {
        this.paramValidatorTypesJson = paramValidatorTypesJson;
    }

    public String getParamDeriverTypesJson() {
        return paramDeriverTypesJson;
    }

    public void setParamDeriverTypesJson(String paramDeriverTypesJson) {
        this.paramDeriverTypesJson = paramDeriverTypesJson;
    }

    public String getDataFormatTypesJson() {
        return dataFormatTypesJson;
    }

    public void setDataFormatTypesJson(String dataFormatTypesJson) {
        this.dataFormatTypesJson = dataFormatTypesJson;
    }

    public String getResourceManagerTypesJson() {
        return resourceManagerTypesJson;
    }

    public void setResourceManagerTypesJson(String resourceManagerTypesJson) {
        this.resourceManagerTypesJson = resourceManagerTypesJson;
    }

    public String getServiceExecutorTypesJson() {
        return serviceExecutorTypesJson;
    }

    public void setServiceExecutorTypesJson(String serviceExecutorTypesJson) {
        this.serviceExecutorTypesJson = serviceExecutorTypesJson;
    }

    public String getAuthenticatorTypesJson() {
        return authenticatorTypesJson;
    }

    public void setAuthenticatorTypesJson(String authenticatorTypesJson) {
        this.authenticatorTypesJson = authenticatorTypesJson;
    }

    public String getAuthorizerTypesJson() {
        return authorizerTypesJson;
    }

    public void setAuthorizerTypesJson(String authorizerTypesJson) {
        this.authorizerTypesJson = authorizerTypesJson;
    }

    public String getJobRunnerTypesJson() {
        return jobRunnerTypesJson;
    }

    public void setJobRunnerTypesJson(String jobRunnerTypesJson) {
        this.jobRunnerTypesJson = jobRunnerTypesJson;
    }

    public String getAdminServiceMetadataDirectory() {
        return adminServiceMetadataDirectory;
    }

    public void setAdminServiceMetadataDirectory(String adminServiceMetadataDirectory) {
        this.adminServiceMetadataDirectory = adminServiceMetadataDirectory;
    }

    public String getAdminServiceExecutorTypesJson() {
        return adminServiceExecutorTypesJson;
    }

    public void setAdminServiceExecutorTypesJson(String adminServiceExecutorTypesJson) {
        this.adminServiceExecutorTypesJson = adminServiceExecutorTypesJson;
    }

    public String getDataClientConfigDirectory() {
        return dataClientConfigDirectory;
    }

    public void setDataClientConfigDirectory(String dataClientConfigDirectory) {
        this.dataClientConfigDirectory = dataClientConfigDirectory;
    }

    public String getResourceConfigDirectory() {
        return resourceConfigDirectory;
    }

    public void setResourceConfigDirectory(String resourceConfigDirectory) {
        this.resourceConfigDirectory = resourceConfigDirectory;
    }

    public String getServiceMetadataDirectory() {
        return serviceMetadataDirectory;
    }

    public void setServiceMetadataDirectory(String serviceMetadataDirectory) {
        this.serviceMetadataDirectory = serviceMetadataDirectory;
    }

    public String getJobSpecDirectory() {
        return jobSpecDirectory;
    }

    public void setJobSpecDirectory(String jobSpecDirectory) {
        this.jobSpecDirectory = jobSpecDirectory;
    }
}
