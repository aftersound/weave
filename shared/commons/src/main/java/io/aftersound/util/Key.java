package io.aftersound.util;

public class Key<T> extends AttributeHolder {

    private final String name;
    private Class<T> type;

    private boolean locked;

    private Key(String name) {
        this.name = name;
    }

    public static <T> Key<T> of(String name) {
        return new Key<>(name);
    }

    public static <T> Key<T> of(String name, Class<T> type) {
        Key<T> key = new Key<>(name);
        return key.bind(type);
    }

    public Key<T> bind(Class<T> type) {
        if (!locked) {
            this.type = type;
        }
        return this;
    }

    public Key<T> lock() {
        this.locked = true;
        return this;
    }

    public String name() {
        return name;
    }

    public Class<T> type() {
        return type;
    }

}
