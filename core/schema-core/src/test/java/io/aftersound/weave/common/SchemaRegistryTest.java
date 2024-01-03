package io.aftersound.weave.common;

import io.aftersound.weave.utils.Handle;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SchemaRegistryTest {

    @Test
    public void testHandle() {
        Handle<ResourceRegistry> handle = Handle
                .of(ResourceRegistry.DEFAULT_ID, ResourceRegistry.class)
                .setAndLock(new ResourceRegistry());

        Schema schema = new Schema();
        schema.setName("Person");
        schema.setFields(new ArrayList<>());
        handle.get().register(schema.getName(), schema);

        assertSame(schema, handle.get().get("Person"));
    }

    @Test
    public void testSchemaRegistry() {
        Schema schema = new Schema();
        schema.setName("Person");
        schema.setFields(new ArrayList<>());

        ResourceRegistry resourceRegistry = new ResourceRegistry().register(schema.getName(), schema);
        assertSame(schema, resourceRegistry.get("Person"));
        assertNull(resourceRegistry.get("Inventor"));
    }

}