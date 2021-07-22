package io.aftersound.weave.common.parser;

public class CharacterParser extends FirstRawKeyValueParser<Character> {

    @Override
    protected Character _parse(String rawValue) {
        return rawValue.length() > 0 ? rawValue.charAt(0) : null;
    }

}
