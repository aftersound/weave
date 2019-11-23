package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.metadata.Control;

public interface Validation extends Control {
    Enforcement getEnforcement();
}
