package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FieldDictionaryTest {

    @Test
    public void testFields() {
        assertNotNull(Util.createFieldictionary("Dict", new ArrayList<>()));

        Dictionary<Field> dict = Util.createFieldictionary(
                "Person",
                List.of(
                        Field.stringFieldBuilder("firstName").build(),
                        Field.stringFieldBuilder("lastName").build()
                )
        );

        assertEquals("Person", dict.getName());

        assertEquals(2, dict.all().size());
        assertNotNull(dict.byEntryName("firstName"));
        assertNotNull(dict.byEntryName("lastName"));
        assertNull(dict.byEntryName("role"));

        assertEquals("firstName", dict.getAttribute("firstName", "name"));
        assertInstanceOf(Type.class, dict.getAttribute("firstName", "type"));
        assertEquals("STRING", dict.getAttribute("firstName", "type.name"));
        assertNull(dict.getAttribute("firstName", "friendlyName"));
        assertNull(dict.getAttribute("firstName", "description"));
        assertNull(dict.getAttribute("firstName", "tags"));
        assertNull(dict.getAttribute("firstName", "tags.PII"));
        assertNull(dict.getAttribute("firstName", "type.options"));
        assertNull(dict.getAttribute("firstName", "type.options.minLength"));
        assertNull(dict.getAttribute("firstName", "unknown.attribute"));

        assertNull(dict.getAttribute("role", "type"));
    }


}