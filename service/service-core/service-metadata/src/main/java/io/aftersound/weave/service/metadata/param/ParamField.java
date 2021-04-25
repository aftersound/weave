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

    /**
     * Alias of parameter, optional.
     */
    private String alias;

    private boolean multiValued;

    /**
     * Only when this is multi-valued field
     */
    private String valueDelimiter;

    /**
     * Constraint of parameter, such as whether it's required, optional, etc.
     */
    private Constraint constraint;

    /**
     * Validation control if parameter is expected to be validated in a specific way
     */
    private Validation validation;

    public ParamType getParamType() {
        return paramType;
    }

    public void setParamType(ParamType paramType) {
        this.paramType = paramType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean hasAlias() {
        String name = getName();
        return name != null && alias != null && alias.length() > 0 && !alias.equals(name);
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

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    @SuppressWarnings("unchecked")
    public <VALIDATION extends Validation> VALIDATION validation() {
        return (VALIDATION) validation;
    }

    public Validation getValidation() {
        return validation;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

}
