package io.aftersound.weave.component;

import io.aftersound.weave.metadata.Control;

/**
 * Conceptual component config
 */
public interface ComponentConfig extends Control {

    /**
     * @return unique identifier of target component created from this config
     */
    String getId();

    /**
     * @return the signature of this config
     */
    Signature signature();

}
