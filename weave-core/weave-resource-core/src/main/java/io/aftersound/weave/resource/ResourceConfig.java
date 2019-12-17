package io.aftersound.weave.resource;

import io.aftersound.weave.metadata.Control;

public interface ResourceConfig extends Control {

    /**
     * @return name of resource if pairing {@link ResourceManager} successfully creates one
     *         based on this resource config
     */
    String getResourceName();
}
