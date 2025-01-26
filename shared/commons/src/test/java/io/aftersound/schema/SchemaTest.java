package io.aftersound.schema;

import io.aftersound.dict.Dictionary;
import io.aftersound.func.*;
import io.aftersound.msg.Message;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SchemaTest {

    @Test
    public void testSchema() {
        final FuncFactory funcFactory = new MasterFuncFactory(
                new ChainFuncFactory(),
                new IntegerFuncFactory(),
                new StringFuncFactory(),
                new MapFuncFactory(),
                new ParseFuncFactory()
        );

        final Schema schema = Schema.of(
                "Consumer",
                List.of(
                        Field.stringFieldBuilder("firstName")
                                .withTypeOption("minLength", 1)
                                .withTypeOption("maxLength", 64)
                                .withFriendlyName("First Name")
                                .withDescription("The first name of the person")
                                .withConstraint(Constraint.required())
                                .withTag("PII", "FIRST_NAME")
                                .withDirectives(
                                        List.of(
                                                Directive.builder("i", "INPUT", "MAP:GET(first_name)").build(),
                                                Directive.builder("v1", "VALIDATION", "MAP:HAS_VALUE(firstName)")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("001")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'firstName': required")
                                                                        .build()
                                                        )
                                                        .build(),
                                                Directive.builder("v2", "VALIDATION", "CHAIN(MAP:GET(firstName),STR:LENGTH_WITHIN(1,64))")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("002")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'firstName': length must be between 1 and 64")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),
                        Field.stringFieldBuilder("lastName")
                                .withTypeOption("minLength", 1)
                                .withTypeOption("maxLength", 64)
                                .withFriendlyName("Last Name")
                                .withDescription("The last name of the person")
                                .withConstraint(Constraint.required())
                                .withTag("PII", "LAST_NAME")
                                .withDirectives(
                                        List.of(
                                                Directive.builder("i", "INPUT", "MAP:GET(last_name)").build(),
                                                Directive.builder("v1", "VALIDATION", "MAP:HAS_VALUE(lastName)")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("001")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'lastName': is required")
                                                                        .build()
                                                        )
                                                        .build(),
                                                Directive.builder("v2", "VALIDATION", "CHAIN(MAP:GET(lastName),STR:LENGTH_WITHIN(1,64))")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("002")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'lastName': length must be between 1 and 64")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                )
                                .build(),
                        Field.integerFieldBuilder("age")
                                .withTypeOption("min", 21)
                                .withFriendlyName("Age")
                                .withDescription("The age of the person")
                                .withConstraint(Constraint.required())
                                .withTag("PII", "AGE")
                                .withDirectives(
                                        List.of(
                                                Directive.builder("i", "INPUT", "MAP:GET(age)").build(),
                                                Directive.builder("v1", "VALIDATION", "MAP:HAS_VALUE(age)")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("001")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'age': required")
                                                                        .build()
                                                        )
                                                        .build(),
                                                Directive.builder("v2", "VALIDATION", "CHAIN(MAP:GET(age),INT:GE(21))")
                                                        .withMessage(
                                                                Message.builder()
                                                                        .withCode("001")
                                                                        .withSeverity("ERROR")
                                                                        .withCategory("REQUEST")
                                                                        .withContent("'age': must be >= 21")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                )
                                .build()
                )
        );
        schema.setKind("FSL");
        schema.setDirectives(null);

        assertEquals("FSL", schema.getKind());
        assertEquals("Consumer", schema.getName());
        assertEquals(3, schema.getFields().size());
        assertNull(schema.getDirectives());

        assertThrows(
                IllegalStateException.class,
                () -> schema.dictionary()
        );

        assertThrows(
                IllegalStateException.class,
                () -> schema.directives()
        );

        schema.init(funcFactory);

        Dictionary<Field> dict = schema.dictionary();
        assertNotNull(dict);
        assertEquals(3, dict.all().size());
        assertEquals("Consumer", dict.getName());
        assertNotNull(dict.byEntryName("firstName"));
        assertNotNull(dict.byEntryName("lastName"));
        assertNotNull(dict.byEntryName("age"));

        Dictionary<Directive> directives = schema.directives();
        assertNotNull(directives);
        assertTrue(directives.all().isEmpty());

        Map<String, Object> input = Map.of("first_name", "Nikola", "last_name", "Tesla", "age", 21);

        // parse in according to schema
        Map<String, Object> parsed = new LinkedHashMap<>();
        parsed.put(
                "firstName",
                dict.byEntryName("firstName").directives().filter(d -> "INPUT".equals(d.getCategory())).get(0).function().apply(input)
        );
        parsed.put(
                "lastName",
                dict.byEntryName("lastName").directives().filter(d -> "INPUT".equals(d.getCategory())).get(0).function().apply(input)
        );
        parsed.put(
                "age",
                dict.byEntryName("age").directives().filter(d -> "INPUT".equals(d.getCategory())).get(0).function().apply(input)
        );

        // validation in according to schema
        Map<String, Boolean> firstNameValidationResults = new LinkedHashMap<>();
        dict.byEntryName("firstName").directives().filter(d -> "VALIDATION".equals(d.getCategory())).forEach(
                d -> firstNameValidationResults.put(d.getLabel(), d.<Map<String, Object>, Boolean>function().apply(parsed))
        );
        assertTrue(firstNameValidationResults.get("v1"));
        assertTrue(firstNameValidationResults.get("v2"));

        Map<String, Boolean> lastNameValidationResults = new LinkedHashMap<>();
        dict.byEntryName("lastName").directives().filter(d -> "VALIDATION".equals(d.getCategory())).forEach(
                d -> lastNameValidationResults.put(d.getLabel(), d.<Map<String, Object>, Boolean>function().apply(parsed))
        );
        assertTrue(lastNameValidationResults.get("v1"));
        assertTrue(lastNameValidationResults.get("v2"));

        Map<String, Boolean> ageValidationResults = new LinkedHashMap<>();
        dict.byEntryName("age").directives().filter(d -> "VALIDATION".equals(d.getCategory())).forEach(
                d -> ageValidationResults.put(d.getLabel(), d.<Map<String, Object>, Boolean>function().apply(parsed))
        );
        assertTrue(ageValidationResults.get("v1"));
        assertTrue(ageValidationResults.get("v2"));
    }

}