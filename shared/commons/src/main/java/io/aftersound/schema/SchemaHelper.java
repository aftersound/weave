package io.aftersound.schema;

import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;

public class SchemaHelper {

    public static final String DEFAULT_SCHEMA_REGISTRY = "schema-registry";

    /**
     * Set up the default schema registry
     *
     * @return the default schema registry which has been successfully set up
     */
    public static ResourceRegistry setupDefaultSchemaRegistry() {
        return Handle.of(DEFAULT_SCHEMA_REGISTRY, ResourceRegistry.class).setAndLock(new ResourceRegistry()).get();
    }

    /**
     * Set up a schema registry with specified id
     *
     * @param schemaRegistryId - the id of the schema registry
     * @return the schema registry which has been successfully set up
     */
    public static ResourceRegistry setupSchemaRegistry(String schemaRegistryId) {
        return Handle.of(schemaRegistryId, ResourceRegistry.class).setAndLock(new ResourceRegistry()).get();
    }

    /**
     * Register given schema into default schema registry
     *
     * @param id       - the id of the schema
     * @param schema   - the schema to be registered
     * @param <SCHEMA> - the type of schema in generic form
     */
    public static <SCHEMA> void registerSchema(String id, SCHEMA schema) {
        getSchemaRegistry(DEFAULT_SCHEMA_REGISTRY).register(id, schema);
    }

    /**
     * Register given schema into the schema registry with specified id
     *
     * @param id               - the id of the schema
     * @param schema           - the schema to be registered
     * @param schemaRegistryId - the id of the schema registry
     * @param <SCHEMA>         - the type of schema in generic form
     */
    public static <SCHEMA> void registerSchema(String id, SCHEMA schema, String schemaRegistryId) {
        getSchemaRegistry(schemaRegistryId).register(id, schema);
    }

    /**
     * Get schema from default schema registry by id
     *
     * @param schemaId - the id of target schema
     * @param <SCHEMA> - the type of schema in generic form
     * @return the target {@link Schema} with specified id if exists
     */
    public static <SCHEMA> SCHEMA getSchema(String schemaId) {
        return getSchemaRegistry(DEFAULT_SCHEMA_REGISTRY).get(schemaId);
    }

    /**
     * Get schema from specified schema registry by id
     *
     * @param schemaRegistryId - the id of target schema registry
     * @param schemaId         - the id of target schema
     * @param <SCHEMA>         - the type of schema in generic form
     * @return the target {@link Schema} with specified id if exists
     */
    public static <SCHEMA> SCHEMA getSchema(String schemaRegistryId, String schemaId) {
        return getSchemaRegistry(schemaRegistryId).get(schemaId);
    }

    /**
     * Get required schema from default schema registry
     *
     * @param schemaId         - the id of target schema
     * @param <SCHEMA>         - the type of schema in generic form
     * @return the schema with specified id if exists
     */
    public static <SCHEMA> SCHEMA getRequiredSchema(String schemaId) {
        return getRequiredSchema(DEFAULT_SCHEMA_REGISTRY, schemaId);
    }

    /**
     * Get required schema with specified id from specified schema registry
     *
     * @param schemaId         - the id of target schema
     * @param schemaRegistryId - the id of target schema registry
     * @param <SCHEMA>         - the type of schema in generic form
     * @return the schema with specified id if exists
     */
    public static <SCHEMA> SCHEMA getRequiredSchema(String schemaId, String schemaRegistryId) {
        SCHEMA schema = getSchemaRegistry(schemaRegistryId).get(schemaId);
        if (schema == null) {
            throw new IllegalStateException(
                    String.format(
                            "Schema '%s' is not available in schema registry '%s'",
                            schemaId,
                            schemaRegistryId
                    )
            );
        }
        return schema;
    }

    /**
     * Get the schema registry with specified id
     *
     * @param schemaRegistryId - the id of target schema registry
     * @return the schema registry if exists
     */
    private static ResourceRegistry getSchemaRegistry(String schemaRegistryId) {
        ResourceRegistry schemaRegistry = Handle.of(schemaRegistryId, ResourceRegistry.class).get();
        if (schemaRegistry == null) {
            throw new IllegalStateException(
                    String.format("Schema Registry with id '%s' is not available", schemaRegistryId)
            );
        }
        return schemaRegistry;
    }

}
