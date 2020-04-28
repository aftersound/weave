package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public class FloatParser extends ValueParser<Float> {

    @Override
    public Float parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        try {
            return Float.parseFloat(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
