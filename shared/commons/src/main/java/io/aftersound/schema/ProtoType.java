package io.aftersound.schema;

import java.util.Map;

public final class ProtoType {

    public static final int PRIMITIVE = 0b0001;
    public static final int CONTAINER = 0b0010;

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
        return (PRIMITIVE & indicator) == PRIMITIVE;
    }

    public boolean isContainer() {
        return (PRIMITIVE & CONTAINER) == CONTAINER;
    }

    public Type create() {
        return create(null);
    }

    public Type create(Map<String, Object> options) {
        return Type.builder(name).withOptions(options).build();
    }

}
