package io.aftersound.weave.service.metadata;

import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.rl.RateLimitControl;
import io.aftersound.weave.service.security.AuthControl;

import java.util.List;
import java.util.Set;

public class ServiceMetadata {

    private String description;
    private String path;
    private Set<String> methods;
    private List<ParamField> paramFields;
    private ExecutionControl executionControl;
    private CacheControl cacheControl;
    private AuthControl authControl;
    private RateLimitControl rateLimitControl;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public void setMethods(Set<String> methods) {
        this.methods = methods;
    }

    public List<ParamField> getParamFields() {
        return paramFields;
    }

    public void setParamFields(List<ParamField> paramFields) {
        this.paramFields = paramFields;
    }

    @SuppressWarnings("unchecked")
    public <E extends ExecutionControl> E getExecutionControl() {
        return (E) executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public AuthControl getAuthControl() {
        return authControl;
    }

    public void setAuthControl(AuthControl authControl) {
        this.authControl = authControl;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    public RateLimitControl getRateLimitControl() {
        return rateLimitControl;
    }

    public void setRateLimitControl(RateLimitControl rateLimitControl) {
        this.rateLimitControl = rateLimitControl;
    }

}
