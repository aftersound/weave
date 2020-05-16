package io.aftersound.weave.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class ValueParser<T> {

    private List<String> rawKeys;

    private T defaultValue;

    public final <VP extends ValueParser<T>> VP rawKeys(List<String> rawKeys) {
        this.rawKeys = rawKeys != null ? Collections.unmodifiableList(rawKeys) : Collections.<String>emptyList();
        return (VP)this;
    }

    public final String firstRawKey() {
        if (rawKeys == null || rawKeys.isEmpty()) {
            throw new IllegalStateException("raw keys is null or empty");
        }
        return rawKeys().get(0);
    }

    public final List<String> rawKeys() {
        if (rawKeys == null || rawKeys.isEmpty()) {
            throw new IllegalStateException("raw keys is null or empty");
        }
        return rawKeys;
    }

    public final <VP extends ValueParser<T>> VP defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return (VP)this;
    }

    public final T defaultValue() {
        return defaultValue;
    }

    /**
     * Parse value in type of T from given raw values
     * @param rawValues
     *          - raw values
     * @return value in type of T
     */
    public abstract T parse(Map<String, String> rawValues);

}
