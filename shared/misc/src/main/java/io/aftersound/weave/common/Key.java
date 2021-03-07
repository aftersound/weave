package io.aftersound.weave.common;


import io.aftersound.weave.common.parser.VoidParser;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Key of a configuration entry
 *
 * @param <T> - generic type of value associated with the key
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
    private boolean required;

    /**
     * description of this {@link Key}
     */
    private String description;

    /**
     * {@link Pattern} used to match and identify key
     */
    private Pattern pattern;

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

    public Key<T> description(String description) {
        if (!locked) {
            this.description = description;
        }
        return this;
    }

    public Key<T> withTag(String tag) {
        if (!locked) {
            tags.add(tag);
        }
        return this;
    }

    public Key<T> withPattern(String regex) {
        if (!locked) {
            this.pattern = Pattern.compile(regex);
        }
        return this;
    }

    /**
     * Create a new key with namespace based off this key
     *
     * @param namespace namespace of new key
     * @return the new key with a name with namespace
     */
    public Key<T> withNamespace(String namespace) {
        return Key.of(namespace + ":" + name, this.valueParser);
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

    public String description() {
        return description;
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public Pattern pattern() {
        return pattern;
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
     * Check if a given candidate key name matches this key
     * @param candidateKeyName  candidate key name
     * @return true it's a match
     */
    public boolean match(String candidateKeyName) {
        return this.pattern != null ? this.pattern.matcher(candidateKeyName).matches() : this.name.equals(candidateKeyName);
    }

    /**
     * Get value associated with this key from config value holder
     *
     * @param valueHolder - config value holder
     * @return config entry value associated with this key
     */
    public T valueFrom(Map<String, Object> valueHolder) {
        return (T) valueHolder.get(name);
    }
}
