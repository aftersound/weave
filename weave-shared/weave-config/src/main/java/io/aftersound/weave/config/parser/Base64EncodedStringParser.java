package io.aftersound.weave.config.parser;

import io.aftersound.weave.config.ValueParser;
import io.aftersound.weave.utils.Base64;

import java.util.Map;

public class Base64EncodedStringParser extends ValueParser<String> {

    @Override
    public String parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        return new String(Base64.getDecoder().decode(rawValue));
    }

}
