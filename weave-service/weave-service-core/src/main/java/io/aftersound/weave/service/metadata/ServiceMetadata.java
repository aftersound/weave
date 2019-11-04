package io.aftersound.weave.service.metadata;

import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.security.SecurityControl;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.List;

public class ServiceMetadata {

    private String id;
    private List<ParamField> paramFields;
    private ExecutionControl executionControl;
    private CacheControl cacheControl;
    private SecurityControl securityControl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public SecurityControl getSecurityControl() {
        return securityControl;
    }

    public void setSecurityControl(SecurityControl securityControl) {
        this.securityControl = securityControl;
    }
}
