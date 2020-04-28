package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public class LongParser extends ValueParser<Long> {

    @Override
    public Long parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        try {
            return Long.parseLong(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
