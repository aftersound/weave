package io.aftersound.weave.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaRegistry {

    public static final String DEFAULT_ID = "DEFAULT_SCHEMA_REGISTRY";

    private final Map<String, Schema> schemaByName = new ConcurrentHashMap<>();

    public SchemaRegistry register(Schema schema) {
        schemaByName.putIfAbsent(schema.getName(), schema);
        return this;
    }

    public Schema get(String name) {
        return schemaByName.get(name);
    }

}
