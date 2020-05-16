package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.metadata.Control;

public interface Validation extends Control {

    /**
     * Paired io.aftersound.weave.service.request.Validator should
     *  do weak validation if enforcement is missing or Weak
     *  do String validation if enforcement is explicitly Strong
     * @return level of enforcement
     */
    Enforcement getEnforcement();
}
