package io.aftersound.weave.service.metadata.param;

import java.io.Serializable;

public class Validation implements Serializable {

    /**
     *  do weak validation if enforcement is missing or Weak
     *  do String validation if enforcement is explicitly Strong
     */
    private Enforcement enforcement;
    private String spec;

    public Enforcement getEnforcement() {
        return enforcement;
    }

    public void setEnforcement(Enforcement enforcement) {
        this.enforcement = enforcement;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

}
