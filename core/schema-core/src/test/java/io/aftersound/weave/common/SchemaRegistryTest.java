package io.aftersound.weave.common;

import io.aftersound.weave.utils.Handle;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SchemaRegistryTest {

    @Test
    public void testHandle() {
        Handle<SchemaRegistry> handle = Handle
                .of(SchemaRegistry.DEFAULT_ID, SchemaRegistry.class)
                .setAndLock(new SchemaRegistry());

        Schema schema = new Schema();
        schema.setName("Person");
        schema.setFields(new ArrayList<>());
        handle.get().register(schema);

        assertSame(schema, handle.get().get("Person"));
    }

    @Test
    public void testSchemaRegistry() {
        Schema schema = new Schema();
        schema.setName("Person");
        schema.setFields(new ArrayList<>());

        SchemaRegistry schemaRegistry = new SchemaRegistry().register(schema);
        assertSame(schema, schemaRegistry.get("Person"));
        assertNull(schemaRegistry.get("Inventor"));
    }

}