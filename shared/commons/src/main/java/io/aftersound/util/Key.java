package io.aftersound.util;

import io.aftersound.func.Func;

import java.util.Map;

public class Key<T> {

    private final String name;

    private final Class<T> type;

    private final AttributeHolder attributes;

    private T defaultValue;

    private String description;

    private Func<Map<String, Object>, T> parseFunc;

    private boolean locked;

    private Key(String name, Class<T> type) {
        this.name = name;
        this.type = type;
        this.attributes = new AttributeHolder();
    }

    public static <T> Key<T> of(String name, Class<T> type) {
        return new Key<>(name, type);
    }

    public static <T> Key<T> of(String name) {
        return new Key<>(name, null);
    }

    public static <T> Key<T> of(String name, Class<T> type, T defaultValue) {
        Key<T> key = new Key<>(name, type);
        return key.bindDefaultValue(defaultValue);
    }

    @SuppressWarnings("unchecked")
    public <V> Key<V> bindDefaultValue(T defaultValue) {
        if (!locked) {
            this.defaultValue = defaultValue;
        }
        return (Key<V>) this;
    }

    @SuppressWarnings("unchecked")
    public <V> Key<V> bindParseFunc(Func<Map<String, Object>, T> parseFunc) {
        if (!locked) {
            this.parseFunc = parseFunc;
        }
        return (Key<V>) this;
    }

    @SuppressWarnings("unchecked")
    public <V> Key<V> withDescription(String description) {
        this.description = description;
        return (Key<V>) this;
    }

    @SuppressWarnings("unchecked")
    public <V,A> Key<V> withAttribute(Key<A> attrKey, A attrValue) {
        if (!locked) {
            this.attributes.set(attrKey, attrValue);
        }
        return (Key<V>) this;
    }

    @SuppressWarnings("unchecked")
    public <V> Key<V> withAttributes(AttributeHolder attributes) {
        if (!locked) {
            this.attributes.acquire(attributes);
        }
        return (Key<V>) this;
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

    @SuppressWarnings("unchecked")
    public <V> V defaultValue() {
        return (V) defaultValue;
    }

    public String description() {
        return description;
    }

    @SuppressWarnings("unchecked")
    public <V> Func<Map<String, Object>, V> parseFunc() {
        return (Func<Map<String, Object>, V>) parseFunc;
    }

    public AttributeHolder attributes() {
        return attributes;
    }

    public <A> A getAttribute(Key<A> attrKey) {
        return attributes.get(attrKey);
    }

    public <A> A getAttribute(Key<A> attrKey, A defaultValue) {
        A attrValue = attributes.get(attrKey);
        return attrValue != null ? attrValue : defaultValue;
    }

    public <A> boolean hasAttribute(Key<A> attrKey, A attrValue) {
        return attrValue.equals(attributes.get(attrKey));
    }

    /**
     * Get value associated with this key from config value holder
     *
     * @param valueHolder - config value holder
     * @return config entry value associated with this key
     */
    @SuppressWarnings("unchecked")
    public T valueFrom(Map<String, Object> valueHolder) {
        return (T) valueHolder.get(name);
    }

}
