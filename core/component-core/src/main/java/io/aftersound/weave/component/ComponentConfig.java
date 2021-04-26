package io.aftersound.weave.component;

import io.aftersound.weave.metadata.Control;

/**
 * Conceptual component config
 */
public abstract class ComponentConfig implements Control {

    private String type;
    private String id;

    @Override
    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    /**
     * @return unique identifier of target component created from this config
     */
    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

}
