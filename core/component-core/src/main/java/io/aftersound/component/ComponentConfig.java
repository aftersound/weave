package io.aftersound.component;

import io.aftersound.metadata.Control;

import java.util.Set;

/**
 * Conceptual component config
 */
public abstract class ComponentConfig implements Control {

    /**
     * type of target component
     */
    private String type;

    /**
     * unique identifier of target component created from this config
     */
    private String id;

    /**
     * optional tags of target component created from this config
     */
    private Set<String> tags;

    @Override
    public final String getType() {
        return type;
    }

    public final void setType(String type) {
        this.type = type;
    }

    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
