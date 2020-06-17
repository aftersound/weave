package io.aftersound.weave.common.parser;


public class StringParser extends FirstRawKeyValueParser<String> {

    @Override
    protected String _parse(String rawValue) {
        return rawValue;
    }

}
