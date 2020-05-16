package io.aftersound.weave.common;

public class Key<VT> {

    private final String name;
    private final Class<VT> valueType;

    private Key(String name, Class<VT> valueType) {
        this.name = name;
        this.valueType = valueType;
    }

    public static <VT> Key<VT> as(String name, Class<VT> valueType) {
        return new Key(name, valueType);
    }

    public String name() {
        return name;
    }

    public Class<VT> valueType() {
        return valueType;
    }

}
