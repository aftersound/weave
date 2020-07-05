package io.aftersound.weave.service.request;

import java.util.List;

public final class VoidValueParser implements ValueParser {

    public static final ValueParser INSTANCE = new VoidValueParser();

    private VoidValueParser() {
    }

    @Override
    public Object parseMultiValues(List<String> rawValues) {
        return null;
    }

    @Override
    public Object parseSingleValue(List<String> rawValues) {
        return null;
    }

}
