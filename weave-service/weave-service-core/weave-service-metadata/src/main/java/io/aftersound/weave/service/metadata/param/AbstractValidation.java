package io.aftersound.weave.service.metadata.param;

public abstract class AbstractValidation implements Validation {

    private Enforcement enforcement;

    @Override
    public Enforcement getEnforcement() {
        return enforcement;
    }

    public void setEnforcement(Enforcement enforcement) {
        this.enforcement = enforcement;
    }
}
