package io.aftersound.weave.config.parser;


public class IntegerParser extends FirstRawKeyValueParser<Integer> {

    @Override
    protected Integer _parse(String rawValue) {
        try {
            return Integer.parseInt(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
