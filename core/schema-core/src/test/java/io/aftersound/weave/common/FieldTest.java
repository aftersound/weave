package io.aftersound.weave.common;

import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class FieldTest {

    @Test
    public void builder() {
        Field f;

        // birthdate
        f = Field.builder("birthdate", TypeEnum.STRING.createType())
                .withTypeOption("format", "yyyy-MM-dd")
                .withConstraint(Constraint.required())
                .withFunc("MAP:GET(date_of_birth)")
                .withDescription("user's birth date")
                .withValues(null)
                .withValidations(
                        Arrays.asList(
                                new Validation(
                                        "MAP:HAS_VALUE(birthdate)",
                                        new Message("001", "'birthdate': value cannot be null")
                                )
                        )
                )
                .build();

        assertEquals("birthdate", f.getName());

        assertNotNull(f.getType());
        assertEquals("STRING", f.getType().getName());
        assertNotNull(f.getType().getOptions());
        assertEquals("yyyy-MM-dd", f.getType().getOptions().get("format"));

        assertNotNull(f.getConstraint());
        assertNotNull(f.constraint());
        assertSame(f.getConstraint(), f.constraint());
        assertEquals(Constraint.Type.Required, f.getConstraint().getType());

        assertNull(f.getValues());

        assertEquals("MAP:GET(date_of_birth)", f.getFunc());

        assertEquals("user's birth date", f.getDescription());

        assertNotNull(f.getValidations());
        assertEquals(1, f.getValidations().size());

        assertNull(f.getHints());
        assertFalse(f.primary());
        assertFalse(f.notNullable());

        // id
        f = Field.builder("id", TypeEnum.STRING.createType())
                .withTypeOptions(MapBuilder.hashMap().kv("pattern", "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$").build())
                .withConstraint(Constraint.required())
                .primary()
                .notNullable()
                .build();

        assertEquals("id", f.getName());

        assertNotNull(f.getType());
        assertEquals("STRING", f.getType().getName());
        assertNotNull(f.getType().getOptions());
        assertEquals("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", f.getType().getOptions().get("pattern"));

        assertNotNull(f.getConstraint());
        assertNotNull(f.constraint());
        assertSame(f.getConstraint(), f.constraint());
        assertEquals(Constraint.Type.Required, f.getConstraint().getType());

        assertNotNull(f.getHints());
        assertTrue(f.primary());
        assertTrue(f.notNullable());

        // key
        f = Field.builder("key", TypeEnum.LONG.createType())
                .notNullable()
                .primary()
                .build();

        assertNotNull(f.getHints());
        assertTrue(f.primary());
        assertTrue(f.notNullable());
    }

    @Test
    public void arrayFieldBuilder() {
        Field f = Field.arrayFieldBuilder("fn", TypeEnum.STRING.createType()).build();
        assertEquals("fn", f.getName());
        assertEquals("ARRAY", f.getType().getName());
        assertEquals("STRING", f.getType().getElementType().getName());
    }

    @Test
    public void booleanFieldBuilder() {
        Field f = Field.booleanFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("BOOLEAN", f.getType().getName());
    }

    @Test
    public void bytesFieldBuilder() {
        Field f = Field.bytesFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("BYTES", f.getType().getName());
    }

    @Test
    public void charFieldBuilder() {
        Field f = Field.charFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("CHAR", f.getType().getName());
    }

    @Test
    public void dateFieldBuilder() {
        Field f = Field.dateFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("DATE", f.getType().getName());
    }

    @Test
    public void doubleFieldBuilder() {
        Field f = Field.doubleFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("DOUBLE", f.getType().getName());
    }

    @Test
    public void intFieldBuilder() {
        Field f = Field.integerFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("INTEGER", f.getType().getName());
    }

    @Test
    public void floatFieldBuilder() {
        Field f = Field.floatFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("FLOAT", f.getType().getName());
    }

    @Test
    public void listFieldBuilder() {
        Field f = Field.listFieldBuilder("fn", TypeEnum.STRING.createType()).build();
        assertEquals("fn", f.getName());
        assertEquals("LIST", f.getType().getName());
        assertEquals("STRING", f.getType().getElementType().getName());
    }

    @Test
    public void longFieldBuilder() {
        Field f = Field.longFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("LONG", f.getType().getName());
    }

    @Test
    public void mapFieldBuilder() {
        Field f = Field.mapFieldBuilder(
                "fn",
                TypeEnum.STRING.createType(),
                TypeEnum.INTEGER.createType()
        ).build();
        assertEquals("fn", f.getName());
        assertEquals("MAP", f.getType().getName());
        assertEquals("STRING", f.getType().getKeyType().getName());
        assertEquals("INTEGER", f.getType().getValueType().getName());
    }

    @Test
    public void objectFieldBuilder() {
        Field f = Field.objectFieldBuilder(
                "fn",
                Field.stringFieldBuilder("firstName").build(),
                Field.stringFieldBuilder("lastName").build()
        ).build();
        assertEquals("fn", f.getName());
        assertEquals("OBJECT", f.getType().getName());
        assertEquals(2, f.getType().getFields().size());
    }

    @Test
    public void setFieldBuilder() {
        Field f = Field.setFieldBuilder("fn", TypeEnum.STRING.createType()).build();
        assertEquals("fn", f.getName());
        assertEquals("SET", f.getType().getName());
        assertEquals("STRING", f.getType().getElementType().getName());
    }

    @Test
    public void shortFieldBuilder() {
        Field f = Field.shortFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("SHORT", f.getType().getName());
    }

    @Test
    public void stringFieldBuilder() {
        Field f = Field.stringFieldBuilder("fn").build();
        assertEquals("fn", f.getName());
        assertEquals("STRING", f.getType().getName());
    }
}