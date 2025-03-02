package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.msg.Message;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.Field;
import io.aftersound.schema.Schema;
import io.aftersound.schema.SchemaHelper;
import io.aftersound.util.MapBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MapFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void mapContainsFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap().put("firstName", "Tesla").put("lastName", "Nikola").build();
        Func<Map<String, Object>, Boolean> f;

        f = masterFuncFactory.create("MAP:CONTAINS(firstName)");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("MAP:CONTAINS(lastName)");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("MAP:CONTAINS(is_inventor)");
        assertFalse(f.apply(m));
    }

    @Test
    public void mapGetFunc() {
        Map<String, Object> m = MapBuilder.<String, Object>hashMap().put("firstName", "Tesla").put("lastName", "Nikola").build();
        Func<Map<String, Object>, Object> vf;

        vf = masterFuncFactory.create("MAP:GET(firstName)");
        assertEquals("Tesla", vf.apply(m));

        vf = masterFuncFactory.create("MAP:GET(lastName)");
        assertEquals("Nikola", vf.apply(m));
    }

    @Test
    public void mapHasAnyValueFunc() {
        Func<Map<String, Object>, Boolean> f = masterFuncFactory.create("MAP:HAS_ANY_VALUE(lastName,Tesla,Edison)");
        assertTrue(f.apply(MapBuilder.<String, Object>hashMap().put("firstName", "Nikola").put("lastName", "Tesla").build()));
        assertFalse(f.apply(MapBuilder.<String, Object>hashMap().put("firstName", "Nikola").put("lastName", "Cage").build()));

        f = masterFuncFactory.create("MAP:HAS_ANY_VALUE(lastName,STR(Tesla),STR(Edison))");
        assertTrue(f.apply(MapBuilder.<String, Object>hashMap().put("firstName", "Nikola").put("lastName", "Tesla").build()));
        assertFalse(f.apply(MapBuilder.<String, Object>hashMap().put("firstName", "Nikola").put("lastName", "Cage").build()));

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("MAP:HAS_ANY_VALUE()")
        );
    }

    @Test
    public void mapHasKeyFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap().put("firstName", "Tesla").put("lastName", "Nikola").build();
        Func<Map<String, Object>, Boolean> f;

        f = masterFuncFactory.create("MAP:HAS_KEY(firstName)");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("MAP:HAS_KEY(lastName)");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("MAP:HAS_KEY(is_inventor)");
        assertFalse(f.apply(m));
    }

    @Test
    public void mapHasValueFunc() {
        Map<String, Object> m = MapBuilder.<String, Object>hashMap().put("firstName", "Tesla").put("lastName", "Nikola").build();
        Func<Map<String, Object>, Boolean> f;

        f = masterFuncFactory.create("MAP:HAS_VALUE(firstName,Tesla)");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("MAP:HAS_VALUE(lastName,Nikola)");
        assertTrue(f.apply(m));
    }

    @Test
    public void mapPutFunc() {
        Func<Map<String, Object>, Map<String, Object>> f = masterFuncFactory.create("MAP:PUT(inventorCount,INT(300))");
        Map<String, Object> m = f.apply(
                MapBuilder.<String, Object>hashMap()
                        .put("firstName", "Tesla")
                        .put("lastName", "Nikola")
                        .buildModifiable()
        );
        assertEquals(300, m.get("inventorCount"));

        f = masterFuncFactory.create("MAP:PUT(invention,Tesla Coil)");
        m = f.apply(
                MapBuilder.<String, Object>hashMap()
                        .put("firstName", "Tesla")
                        .put("lastName", "Nikola")
                        .buildModifiable()
        );
        assertEquals("Tesla Coil", m.get("invention"));
    }

    @Test
    public void mapReadFunc() {
        Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Tesla")
                .put("lastName", "Nikola")
                .put("inventions", Arrays.asList("AC", "The Tesla Coil"))
                .build();
        Func<Map<String, Object>, Map<String, Object>> vf;

        vf = masterFuncFactory.create("MAP:READ(firstName,lastName)");
        Map<String, Object> r = vf.apply(m);

        assertNotNull(r);
        assertEquals(2, r.size());
        assertEquals("Tesla", r.get("firstName"));
        assertEquals("Nikola", r.get("lastName"));
    }

    @Test
    public void mapValidateFunc() {
        // initialize handle of SchemaRegistry
        Schema schema = new Schema();
        schema.setName("Person");
        schema.setFields(
                Arrays.asList(
                        Field.stringFieldBuilder("firstName").withConstraint(Constraint.required()).build(),
                        Field.stringFieldBuilder("lastName").withConstraint(Constraint.required()).build()
                )
        );
        schema.init(masterFuncFactory);

        SchemaHelper.setupDefaultSchemaRegistry();
        SchemaHelper.registerSchema(schema.getName(), schema);

        Func<Map<String, Object>, List<Message>> f = masterFuncFactory.create("MAP:VALIDATE(Person)");
        List<Message> messages = f.apply(
                MapBuilder.<String, Object>hashMap()
                        .put("firstName", "Tesla")
                        .put("lastName", "Nikola")
                        .put("inventions", Arrays.asList("AC", "The Tesla Coil"))
                        .build()
        );
        assertEquals(0, messages.size());

        messages = f.apply(new HashMap<>());
        assertEquals(2, messages.size());
    }

    @Test
    public void mapValuesFunc() {
        Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Nikola")
                .put("lastName", "Tesla")
                .put("inventions", Arrays.asList("AC", "The Tesla Coil"))
                .build();

        Func<Map<String, Object>, Collection<Object>> func = masterFuncFactory.create("MAP:VALUES()");
        Collection<Object> values = func.apply(m);
        assertNotNull(values);
        assertEquals(3, values.size());

        func = masterFuncFactory.create("CHAIN(MAP:READ(firstName,lastName),MAP:VALUES())");
        values = func.apply(m);
        assertNotNull(values);
        assertEquals(2, values.size());
        Iterator<Object> iter = values.iterator();
        assertEquals("Nikola", iter.next());
        assertEquals("Tesla", iter.next());
    }

}
