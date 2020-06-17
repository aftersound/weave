package io.aftersound.weave.common;


import io.aftersound.weave.common.parser.VoidParser;

import java.util.*;

/**
 * Key of a configuration entry
 * @param <T>
 *          - generic type of value associated with the key
 */
@SuppressWarnings("unchecked")
public final class Key<T> {

    private final String name;

    /**
     * keys of raw configs from which the config value of this key would be derived
     */
    private final List<String> rawKeys;

    /**
     * value parser which parses T from raw config values
     */
    private final ValueParser<T> valueParser;

    /**
     * tags of this key
     */
    private final Set<String> tags;

    /**
     * indicator of whether the corresponding configuration entry is required or not.
     * default as not required
     */
    private boolean required = false;

    /**
     * description of this {@link Key}
     */
    private String description;

    /**
     * lock to prevent Key from being updated on the fly
     */
    private boolean locked = false;

    private Key(String name, List<String> rawKeys, ValueParser<T> valueParser) {
        this.name = name;
        this.rawKeys = rawKeys;
        this.valueParser = valueParser;
        this.required = false;
        this.tags = new HashSet<>();
    }

    public static <T> Key<T> of(String name) {
        return new Key(
                name,
                Arrays.asList(name),
                new VoidParser()
        );
    }

    public static <T> Key<T> of(String name, ValueParser<T> valueParser) {
        return new Key(name, Arrays.asList(name), valueParser);
    }

    public static <T> Key<T> of(String name, List<String> rawKeys, ValueParser<T> valueParser) {
        return new Key(name, rawKeys, valueParser);
    }

    public String name() {
        return name;
    }

    public List<String> rawKeys() {
        return rawKeys;
    }

    public ValueParser<T> valueParser() {
        return valueParser;
    }

    public Key<T> markAsRequired() {
        if (!locked) {
            required = true;
        }
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public Key<T> withTag(String tag) {
        if (!locked) {
            tags.add(tag);
        }
        return this;
    }

    public Key<T> description(String description) {
        if (!locked) {
            this.description = description;
        }
        return this;
    }

    public String description() {
        return description;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * Key is not supposed to be changed after it's defined.
     * Explicitly lock this Key to prevent it from being changed by mistake
     */
    public Key<T> lock() {
        locked = true;
        return this;
    }

    /**
     * Get value associated with this key from config value holder
     * @param valueHolder
     *          - config value holder
     * @return config entry value associated with this key
     */
    public T valueFrom(Map<String, Object> valueHolder) {
        return (T) valueHolder.get(name);
    }
}
