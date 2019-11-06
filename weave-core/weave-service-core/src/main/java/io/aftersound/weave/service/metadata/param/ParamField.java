package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.metadata.Field;

/**
 * Definition of a request parameter
 */
public class ParamField extends Field {

    /**
     * Type of parameter. See {@link ParamType} for possible values
     */
    private ParamType type;

    /**
     * Constraint of parameter, such as whether it's required, optional, etc.
     */
    private Constraint constraint;

    /**
     * Derivation control if parameter is expected to be derived from other parameter.
     */
    private DeriveControl deriveControl;

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public DeriveControl getDeriveControl() {
        return deriveControl;
    }

    public void setDeriveControl(DeriveControl deriveControl) {
        this.deriveControl = deriveControl;
    }

}
