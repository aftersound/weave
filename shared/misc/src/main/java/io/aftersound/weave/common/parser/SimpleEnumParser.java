package io.aftersound.weave.common.parser;

import java.lang.reflect.Method;

public class SimpleEnumParser<E extends Enum> extends FirstRawKeyValueParser<E> {

    private final Class<E> enumType;

    public SimpleEnumParser(Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    protected E _parse(String rawValue) {
        try {
            Method method = enumType.getMethod("valueOf", String.class);
            return enumType.cast(method.invoke(null, rawValue));
        } catch (Exception e) {
            return null;
        }
    }
}
