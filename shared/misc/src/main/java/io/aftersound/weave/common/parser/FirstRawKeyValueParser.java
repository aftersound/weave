package io.aftersound.weave.common.parser;

import io.aftersound.weave.common.ValueParser;

import java.util.Map;

public abstract class FirstRawKeyValueParser<T> extends ValueParser<T> {

    @Override
    public final T parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        return _parse(rawValue);
    }

    protected abstract T _parse(String rawValue);

}
