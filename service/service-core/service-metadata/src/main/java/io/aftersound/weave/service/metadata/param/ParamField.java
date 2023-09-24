package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.common.Field;

/**
 * Definition of a request parameter
 */
public class ParamField extends Field {

    /**
     * Type of parameter. See {@link ParamType} for possible values
     */
    private ParamType paramType;

    /**
     * Name of parameter. Optional in some scenarios
     * Defined in {@link Field}
     */
    // private String name;

    private boolean multiValued;

    /**
     * Only when this is multi-valued field
     */
    private String valueDelimiter;

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public boolean isMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getValueDelimiter() {
        return valueDelimiter;
    }

    public void setValueDelimiter(String valueDelimiter) {
        this.valueDelimiter = valueDelimiter;
    }

}
