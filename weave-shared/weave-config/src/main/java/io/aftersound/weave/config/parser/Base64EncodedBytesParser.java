package io.aftersound.weave.config.parser;

import io.aftersound.weave.config.ValueParser;
import io.aftersound.weave.utils.Base64;

import java.util.Map;

public class Base64EncodedBytesParser extends ValueParser<byte[]> {

    @Override
    public byte[] parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        return Base64.getDecoder().decode(rawValue);
    }

}
