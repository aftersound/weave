package io.aftersound.schema;

import java.util.Map;

public final class ProtoType {

    public static final int PRIMITIVE = 0b0001;
    public static final int NUMBER = 0b0010;
    public static final int CONTAINER = 0b0100;

    private final String name;
    private final int indicator;

    public ProtoType(String typeName) {
        this.name = typeName;
        this.indicator = 0;
    }

    public ProtoType(String typeName, int indicator) {
        this.name = typeName;
        this.indicator = indicator;
    }

    public String name() {
        return name;
    }

    public boolean isPrimitive() {
        return (indicator & PRIMITIVE) == PRIMITIVE;
    }

    public boolean isNumber() {
        return (indicator & NUMBER) == NUMBER;
    }

    public boolean isContainer() {
        return (indicator & CONTAINER) == CONTAINER;
    }

    public boolean match(String typeName) {
        return name.equals(typeName);
    }

    public boolean matchIgnoreCase(String typeName) {
        return name.equalsIgnoreCase(typeName.toLowerCase());
    }

    public Type create() {
        return create(null);
    }

    public Type create(Map<String, Object> options) {
        return Type.builder(name).withOptions(options).build();
    }

}
