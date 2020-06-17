package io.aftersound.weave.common.parser;

import io.aftersound.weave.common.ValueParser;

import java.util.Map;

public final class VoidParser<T> extends ValueParser<T> {

    @Override
    public T parse(Map<String, String> rawValues) {
        return null;
    }

}
