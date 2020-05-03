package io.aftersound.weave.config.parser;


public class FloatParser extends FirstRawKeyValueParser<Float> {

    @Override
    protected Float _parse(String rawValue) {
        try {
            return Float.parseFloat(rawValue);
        } catch (Exception e) {
            return defaultValue();
        }
    }
}
