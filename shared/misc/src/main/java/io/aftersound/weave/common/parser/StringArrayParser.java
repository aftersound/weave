package io.aftersound.weave.common.parser;

import java.util.regex.Pattern;

public class StringArrayParser extends FirstRawKeyValueParser<String[]> {

    private final Pattern pattern;

    public StringArrayParser(String delimiter) {
        this.pattern = Pattern.compile(delimiter);
    }

    @Override
    protected String[] _parse(String rawValue) {
        return pattern.split(rawValue);
    }

}
