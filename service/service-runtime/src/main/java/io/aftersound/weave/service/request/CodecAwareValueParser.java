package io.aftersound.weave.service.request;

import io.aftersound.weave.codec.Decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class CodecAwareValueParser implements ValueParser {

    private final Decoder<String, ?> decoder;

    CodecAwareValueParser(Decoder<String, ?> decoder) {
        this.decoder = decoder;
    }

    @Override
    public Object parseMultiValues(List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> values = new ArrayList<>();
        for (String rawValue : rawValues) {
            values.add(decoder.decode(rawValue));
        }
        return values;
    }

    @Override
    public Object parseSingleValue(List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return null;
        }

        return decoder.decode(rawValues.get(0));
    }

}
