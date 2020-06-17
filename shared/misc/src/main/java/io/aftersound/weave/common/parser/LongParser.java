package io.aftersound.weave.common.parser;


public class LongParser extends FirstRawKeyValueParser<Long> {

    @Override
    protected Long _parse(String rawValue) {
        try {
            return Long.parseLong(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
