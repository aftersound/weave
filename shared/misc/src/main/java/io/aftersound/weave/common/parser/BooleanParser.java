package io.aftersound.weave.common.parser;

public class BooleanParser extends FirstRawKeyValueParser<Boolean> {

    @Override
    protected Boolean _parse(String rawValue) {
        return Boolean.parseBoolean(rawValue);
    }

}
