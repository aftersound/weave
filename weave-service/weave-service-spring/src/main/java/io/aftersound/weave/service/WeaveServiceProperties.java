package io.aftersound.weave.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeaveServiceProperties {

    @Value("${security-asset-directory}")
    private String securityAssetDirectory;

    @Value("${cache-factory-types-json}")
    private String cacheFactoryTypesJson;

    @Value("${data-client-factory-types-json}")
    private String dataClientFactoryTypesJson;

    @Value("${param-deriver-types-json}")
    private String paramDeriverTypesJson;

    @Value("${service-executor-types-json}")
    private String serviceExecutorTypesJson;

    @Value("${job-runner-types-json}")
    private String jobRunnerTypesJson;

    @Value("${admin-service-metadata-directory}")
    private String adminServiceMetadataDirectory;

    @Value("${admin-service-executor-types-json}")
    private String adminServiceExecutorTypesJson;

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

    public String getServiceMetadataDirectory() {
        return serviceMetadataDirectory;
    }

    public void setServiceMetadataDirectory(String serviceMetadataDirectory) {
        this.serviceMetadataDirectory = serviceMetadataDirectory;
    }

    public String getCacheFactoryTypesJson() {
        return cacheFactoryTypesJson;
    }

    public void setCacheFactoryTypesJson(String cacheFactoryTypesJson) {
        this.cacheFactoryTypesJson = cacheFactoryTypesJson;
    }

    public String getDataClientFactoryTypesJson() {
        return dataClientFactoryTypesJson;
    }

    public void setDataClientFactoryTypesJson(String dataClientFactoryTypesJson) {
        this.dataClientFactoryTypesJson = dataClientFactoryTypesJson;
    }

    public String getParamDeriverTypesJson() {
        return paramDeriverTypesJson;
    }

    public void setParamDeriverTypesJson(String paramDeriverTypesJson) {
        this.paramDeriverTypesJson = paramDeriverTypesJson;
    }

    public String getServiceExecutorTypesJson() {
        return serviceExecutorTypesJson;
    }

    public void setServiceExecutorTypesJson(String serviceExecutorTypesJson) {
        this.serviceExecutorTypesJson = serviceExecutorTypesJson;
    }

    public String getJobRunnerTypesJson() {
        return jobRunnerTypesJson;
    }

    public void setJobRunnerTypesJson(String jobRunnerTypesJson) {
        this.jobRunnerTypesJson = jobRunnerTypesJson;
    }

    public String getJobSpecDirectory() {
        return jobSpecDirectory;
    }

    public void setJobSpecDirectory(String jobSpecDirectory) {
        this.jobSpecDirectory = jobSpecDirectory;
    }

}
