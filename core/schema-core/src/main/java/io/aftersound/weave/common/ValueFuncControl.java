package io.aftersound.weave.common;

import io.aftersound.weave.metadata.Control;

/**
 * A {@link Control} for {@link ValueFuncFactory} to act accordingly to create {@link ValueFunc}
 */
public interface ValueFuncControl extends Control {
    String asValueFuncSpec();
}
