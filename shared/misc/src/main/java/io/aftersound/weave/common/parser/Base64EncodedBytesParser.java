package io.aftersound.weave.common.parser;

import java.util.Base64;

public class Base64EncodedBytesParser extends FirstRawKeyValueParser<byte[]> {

    @Override
    protected byte[] _parse(String rawValue) {
        return Base64.getDecoder().decode(rawValue);
    }

}
