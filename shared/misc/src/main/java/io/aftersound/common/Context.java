package io.aftersound.common;


/**
 * A general purpose dictionary-driven context information container
 */
public final class Context extends AttributeHolder<Context> {

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
