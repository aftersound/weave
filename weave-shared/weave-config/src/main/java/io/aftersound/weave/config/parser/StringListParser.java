package io.aftersound.weave.config.parser;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class StringListParser extends FirstRawKeyValueParser<List<String>> {

    private final Pattern pattern;

    public StringListParser(String delimiter) {
        this.pattern = Pattern.compile(delimiter);
    }

    @Override
    protected List<String> _parse(String rawValue) {
        String[] values = pattern.split(rawValue);
        return Arrays.asList(values);
    }
}
