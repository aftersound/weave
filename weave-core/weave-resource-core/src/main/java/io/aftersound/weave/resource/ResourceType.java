package io.aftersound.weave.resource;

public class ResourceType<R> {

    private final String name;
    private final Class<R> type;

    public ResourceType(String name, Class<R> type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public Class<R> type() {
        return type;
    }

    public String toString() {
        return "Resource { " + name + ", " + type.getName() + "}";
    }
}

