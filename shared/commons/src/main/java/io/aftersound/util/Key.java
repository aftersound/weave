package io.aftersound.util;

import io.aftersound.func.Func;

import java.util.Map;

public class Key<T> extends AttributeHolder {

    private final String name;

    private Class<T> type;
    private T defaultValue;
    private Func<Object, T> valueParser;

    private boolean locked;

    private Key(String name) {
        this.name = name;
    }

    public static <T> Key<T> of(String name) {
        return new Key<>(name);
    }

    public static <T> Key<T> of(String name, Class<T> type) {
        Key<T> key = new Key<>(name);
        return key.bindType(type);
    }

    public static <T> Key<T> of(String name, Class<T> type, T defaultValue) {
        Key<T> key = new Key<>(name);
        return key.bindType(type).bindDefaultValue(defaultValue);
    }

    public Key<T> bindType(Class<T> type) {
        if (!locked) {
            this.type = type;
        }
        return this;
    }

    public Key<T> bindDefaultValue(T defaultValue) {
        if (!locked) {
            this.defaultValue = defaultValue;
        }
        return this;
    }

    public Key<T> bindValueParser(Func<Object, T> valueParser) {
        if (!locked) {
            this.valueParser = valueParser;
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

    public T defaultValue() {
        return defaultValue;
    }

    public Func<Object, T> valueParser() {
        return valueParser;
    }

    public String[] rawKeys() {
        String[] rawKeys = get(Key.of("rawKeys", String[].class));
        if (rawKeys != null && rawKeys.length > 0) {
            return rawKeys;
        } else {
            return new String[] { name };
        }
    }

    public Key<T> withNamespace(String namespace) {
        Key<T> k = new Key<>(namespace + ":" + name);
        return k.bindType(type).bindDefaultValue(defaultValue).bindValueParser(valueParser);
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
