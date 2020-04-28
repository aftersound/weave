package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Map;

public class DoubleParser extends ValueParser<Double> {

    @Override
    public Double parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        try {
            return Double.parseDouble(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
