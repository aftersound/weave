package io.aftersound.dict;

import io.aftersound.schema.Field;
import io.aftersound.schema.FieldAttributeAccessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDictionaryTest {

    @Test
    public void simpleDictionary() {
        assertNotNull(SimpleDictionary.builder().build());

        final Dictionary<Field> dict = SimpleDictionary.<Field>builder("Person")
                .withEntries(
                        List.of(
                            Field.stringFieldBuilder("firstName").build(),
                            Field.stringFieldBuilder("lastName").build()
                        )
                )
                .withEntry(Field.stringFieldBuilder("role").build())
                .withEntry(null)
                .withEntries(null)
                .withEntries(fieldListWithNull())
                .withEntryNameFunc(Field::getName)
                .withAttributeAccessor(new FieldAttributeAccessor())
                .build();

        assertNotNull(dict);
        assertEquals(3, dict.all().size());
        assertEquals("Person", dict.getName());
        assertNotNull(dict.byEntryName("firstName"));
        assertNull(dict.byEntryName("inventions"));
        assertEquals(2, dict.filter(f -> f.getName().contains("Name")).size());
        assertEquals("STRING", dict.getAttribute("firstName", "type.name"));
        assertNull(dict.getAttribute("unknown", "type.name"));
    }

    @Test
    public void simpleDictionary1() {
        final Dictionary<String> dict = SimpleDictionary.<String>builder("Names")
                .withEntries(List.of("Newton", "Einstein"))
                .withEntryNameFunc(String::toLowerCase)
                .build();
        assertNotNull(dict);
        assertEquals(2, dict.all().size());
        assertEquals("Names", dict.getName());
        assertNotNull(dict.byEntryName("newton"));
        assertNull(dict.getAttribute("newton", "discoveries"));
    }

    private List<Field> fieldListWithNull() {
        List<Field> fields = new ArrayList<>();
        fields.add(null);
        return fields;
    }

}