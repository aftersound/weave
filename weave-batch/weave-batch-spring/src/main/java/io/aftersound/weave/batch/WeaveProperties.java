package io.aftersound.weave.batch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeaveProperties {

    // Default in Spring Boot application.properties
    // But override-able via argument
    @Value("${weave.batch.appconfig}")
    private String appConfig;

    @Value("${weave.batch.jobspec}")
    private String jobSpec;

    @Value("${weave.batch.workdir}")
    private String workDir;

    public String getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(String appConfig) {
        this.appConfig = appConfig;
    }

    public String getJobSpec() {
        return jobSpec;
    }

    public void setJobSpec(String jobSpec) {
        this.jobSpec = jobSpec;
    }

    public String getWorkDir() {
        return workDir;
    }

    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
}
