package io.aftersound.common;


import io.aftersound.util.AttributeHolder;

/**
 * A general purpose dictionary-driven context information container
 */
public final class Context extends AttributeHolder {

    private final String id;

    public Context(String id) {
        super();
        this.id = id;
    }

    public Context() {
        this(null);
    }

    public String getId() {
        return id;
    }

}
