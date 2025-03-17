package io.aftersound.service.metadata;

import io.aftersound.service.cache.CacheControl;
import io.aftersound.service.metadata.param.ParamField;
import io.aftersound.service.rl.RateLimitControl;
import io.aftersound.service.security.AuthControl;

import java.util.List;
import java.util.Set;

/**
 * The metadata about an API/service, which
 *  1.defines the interface
 *  2.determines the binding with a specific implementation of ServiceExecutor
 *    by given {@link ExecutionControl#getType()}
 */
public class ServiceMetadata {

    private Set<String> methods;
    private String path;
    private String summary;
    private String description;
    private List<ParamField> paramFields;
    private ExecutionControl executionControl;
    private AuthControl authControl;
    private RateLimitControl rateLimitControl;
    private CacheControl cacheControl;

    public Set<String> getMethods() {
        return methods;
    }

    public void setMethods(Set<String> methods) {
        this.methods = methods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public RateLimitControl getRateLimitControl() {
        return rateLimitControl;
    }

    public void setRateLimitControl(RateLimitControl rateLimitControl) {
        this.rateLimitControl = rateLimitControl;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }
}
