package io.aftersound.weave.service.metadata;

import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.security.AuthControl;

import java.util.List;

public class ServiceMetadata {

    private String path;
    private List<ParamField> paramFields;
    private ExecutionControl executionControl;
    private CacheControl cacheControl;
    private AuthControl authControl;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<ParamField> getParamFields() {
        return paramFields;
    }

    public void setParamFields(List<ParamField> paramFields) {
        this.paramFields = paramFields;
    }

    @SuppressWarnings("unchecked")
    public <E extends ExecutionControl> E getExecutionControl() {
        return (E)executionControl;
    }

    public void setExecutionControl(ExecutionControl executionControl) {
        this.executionControl = executionControl;
    }

    public CacheControl getCacheControl() {
        return cacheControl;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    public AuthControl getAuthControl() {
        return authControl;
    }

    public void setAuthControl(AuthControl authControl) {
        this.authControl = authControl;
    }
}
