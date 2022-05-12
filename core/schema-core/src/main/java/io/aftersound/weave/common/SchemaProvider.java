package io.aftersound.weave.common;

/**
 * Conceptual provider which provides schema
 * @param <SCHEMA>
 */
public interface SchemaProvider<SCHEMA> {
    SCHEMA getSchema(String id);
}
