package io.aftersound.weave.config.parser;


public class DoubleParser extends FirstRawKeyValueParser<Double> {

    @Override
    protected Double _parse(String rawValue) {
        try {
            return Double.parseDouble(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }

}
