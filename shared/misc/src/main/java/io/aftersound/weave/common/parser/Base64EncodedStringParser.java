package io.aftersound.weave.common.parser;

import java.util.Base64;

public class Base64EncodedStringParser extends FirstRawKeyValueParser<String> {

    @Override
    protected String _parse(String rawValue) {
        return new String(Base64.getDecoder().decode(rawValue));
    }

}
