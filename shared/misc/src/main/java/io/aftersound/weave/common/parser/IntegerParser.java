package io.aftersound.weave.common.parser;


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
