package io.aftersound.schema;


import io.aftersound.func.FuncFactory;
import io.aftersound.func.MapFuncFactory;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.msg.Message;
import io.aftersound.util.map.Builder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    @Test
    public void builder() {
        FuncFactory funcFactory = new MasterFuncFactory(
            new MapFuncFactory()
        );

        Field f;

        // birthdate
        f = Field.builder("birthdate", TypeEnum.STRING.createType())
                .withTypeOption("format", "yyyy-MM-dd")
                .withConstraint(Constraint.required())
                .withDescription("user's birth date")
                .withValues(null)
                .withTags(Map.of("PII", "BIRTHDATE"))
                .withDirectives(
                        List.of(
                            Directive.builder("INPUT", "MAP:GET(date_of_birth)").build(),
                            Directive.builder("VALIDATION", "MAP:HAS_VALUE(birthdate)")
                                    .withMessage(
                                            Message.builder()
                                                    .withCode("001")
                                                    .withContent("'birthdate': value cannot be null")
                                                    .build()
                                    )
                                    .build()
                        )
                )
                .withPath("birthdate")
                .build();

        f.initDirectives(funcFactory);

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

        assertNotNull(f.directives().byEntryName("INPUT"));
        assertEquals("MAP:GET(date_of_birth)", f.directives().byEntryName("INPUT").getFunc());

        assertEquals("user's birth date", f.getDescription());

        assertNotNull(f.getDirectives());
        assertEquals(2, f.getDirectives().size());
        assertEquals(2, f.directives().all().size());
        assertNotNull(f.directives().byEntryName("VALIDATION"));

        assertNotNull(f.path());

        assertNotNull(f.getTags());
        assertTrue(f.hasTag("PII", "BIRTHDATE"));
        assertFalse(f.hasTag("Primary"));
        assertFalse(f.hasTag("Primary", Boolean.TRUE));
        assertFalse(f.hasTag("NotNullable"));

        // id
        f = Field.builder("id", TypeEnum.STRING.createType())
                .withTypeOptions(Builder.<String, Object>hashMap().put("pattern", "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$").build())
                .withConstraint(Constraint.required())
                .withTag("Primary", true)
                .withTag("NotNullable", true)
                .build();
        f.initDirectives(funcFactory);

        assertEquals("id", f.getName());

        assertNotNull(f.getType());
        assertEquals("STRING", f.getType().getName());
        assertNotNull(f.getType().getOptions());
        assertEquals("^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$", f.getType().getOptions().get("pattern"));

        assertNotNull(f.getConstraint());
        assertNotNull(f.constraint());
        assertSame(f.getConstraint(), f.constraint());
        assertEquals(Constraint.Type.Required, f.getConstraint().getType());

        assertNotNull(f.getTags());
        assertTrue(f.hasTag("Primary"));
        assertTrue(f.hasTag("Primary", Boolean.TRUE));
        assertTrue(f.hasTag("NotNullable"));
        assertTrue(f.hasTag("NotNullable", Boolean.TRUE));
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