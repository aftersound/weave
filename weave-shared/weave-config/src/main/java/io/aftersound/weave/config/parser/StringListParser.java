package io.aftersound.weave.config.parser;


import io.aftersound.weave.config.ValueParser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StringListParser extends ValueParser<List<String>> {

    private final Pattern pattern;

    public StringListParser(String delimiter) {
        this.pattern = Pattern.compile(delimiter);
    }

    @Override
    public List<String> parse(Map<String, String> rawValues) {
        String rawKey = firstRawKey();
        String rawValue = rawValues.get(rawKey);

        if (rawValue == null) {
            return defaultValue();
        }

        try {
            String[] values = pattern.split(rawValue);
            return Arrays.asList(values);
        } catch (Exception e) {
            return defaultValue();
        }
    }
}
