package io.aftersound.func;

/**
 * Represents an encapsulated context consisting of a root object and a current object.
 * The root object typically serves as the originating context, while the current object
 * represents the active context within the hierarchy.
 */
public class InputContext {

    private final Object root;
    private final Object current;

    public InputContext(Object root, Object current) {
        this.root = root;
        this.current = current;
    }

    public static InputContext of(Object root, Object current) {
        return new InputContext(root, current);
    }

    public Object getRoot() {
        return root;
    }

    public Object getCurrent() {
        return current;
    }

}
