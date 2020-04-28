package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public final class VoidParser<T> extends ValueParser<T> {

    @Override
    public T parse(Map<String, String> rawValues) {
        return null;
    }

}
