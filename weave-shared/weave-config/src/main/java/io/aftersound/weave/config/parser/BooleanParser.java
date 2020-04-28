package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public class BooleanParser extends ValueParser<Boolean> {

    @Override
    public Boolean parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);
        if (rawValue == null) {
            return defaultValue();
        }
        return Boolean.parseBoolean(rawValue);
    }

}
