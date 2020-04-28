package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public class IntegerParser extends ValueParser<Integer> {

    @Override
    public Integer parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        try {
            return Integer.parseInt(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
