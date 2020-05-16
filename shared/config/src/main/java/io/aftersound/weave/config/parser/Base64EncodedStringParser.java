package io.aftersound.weave.config.parser;

import io.aftersound.weave.utils.Base64;

public class Base64EncodedStringParser extends FirstRawKeyValueParser<String> {

    @Override
    protected String _parse(String rawValue) {
        return new String(Base64.getDecoder().decode(rawValue));
    }

}
