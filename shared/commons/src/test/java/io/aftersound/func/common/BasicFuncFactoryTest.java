package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.MapBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class BasicFuncFactoryTest {
    
    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void chainedFunc() {
        assertEquals(
                "20210309",
                masterFuncFactory.create("CHAINED(LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd))")
                        .apply("1615349890000")
        );

        assertEquals(
                "20210309",
                masterFuncFactory.create("CHAINED(DATE:FROM(LONG),DATE:FORMAT(yyyyMMdd))")
                        .apply("1615349890000")
        );

        assertEquals(
                "20210309",
                masterFuncFactory.create(
                        "CHAINED(STR(1615349890000),LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd))"
                ).apply(null)
        );
    }

    @Test
    public void chainFunc() {
        assertEquals(
                "20210309",
                masterFuncFactory.create("CHAIN(LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd))")
                        .apply("1615349890000")
        );

        assertEquals(
                "20210309",
                masterFuncFactory.create("CHAIN(DATE:FROM(LONG),DATE:FORMAT(yyyyMMdd))")
                        .apply("1615349890000")
        );

        assertEquals(
                "20210309",
                masterFuncFactory.create(
                        "CHAIN(STR(1615349890000),LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd))"
                ).apply(null)
        );
    }

    @Test
    public void filterFunc() {
        Func<Object, Object> f = masterFuncFactory.create("FILTER(MAP:GET(is_elite_programmer))");
        Object v = f.apply(MapBuilder.<String, Object>hashMap().put("first_name", "Stanley").put("last_name", "Lippman").put("is_elite_programmer", Boolean.TRUE).build());
        assertInstanceOf(Map.class, v);
        Map<String, Object> m = (Map<String, Object>) v;
        assertEquals("Lippman", m.get("last_name"));
    }

    @Test
    public void nameFunc() {
        Object value = masterFuncFactory.create("NAME(firstName)").apply("Tesla");
        assertInstanceOf(Map.class, value);
        assertEquals("Tesla", ((Map<?, ?>) value).get("firstName"));
    }

    @Test
    public void nullFunc() {
        Func<Object, Object> f = masterFuncFactory.create("NULL()");
        assertNull(f.apply("hello"));
        assertNull(f.apply(1000L));
    }
    @Test
    public void labelFunc() {
        assertEquals(
                "inventor",
                masterFuncFactory.create("LABEL(MAP:HAS_VALUE(is_inventor,Y),STR(inventor))")
                        .apply(MapBuilder.hashMap().put("is_inventor", "Y").build())
        );

        assertNull(
                masterFuncFactory.create("LABEL(MAP:HAS_VALUE(is_inventor,Y),STR(inventor))")
                        .apply(Collections.emptyMap())
        );
    }

    @Test
    public void mappingFunc() {
        Func<Object, Object> f = masterFuncFactory.create("MAPPING(String,Long,a,1000,b,2000,c,3000)");
        assertEquals(1000L, f.apply("a"));
        assertEquals(2000L, f.apply("b"));
        assertEquals(3000L, f.apply("c"));

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("MAPPING(Invalid,Long,a,1000,b,2000,c,3000)")
        );

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("MAPPING(LongList,Long,a,1000,b,2000,c,3000)")
        );

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("MAPPING(String,Long)")
        );

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("MAPPING(String,Long,a,1000,b,2000,c)")
        );
    }

    @Test
    public void passThroughFunc() {
        Func<Object, Object> func = masterFuncFactory.create("_");

        assertEquals(Boolean.TRUE, func.apply(Boolean.TRUE));
        assertEquals("ABC", func.apply("ABC"));
        assertEquals(1d, func.apply(1d));
        assertEquals(1f, func.apply(1f));
        assertEquals(1, func.apply(1));
        assertEquals(1L, func.apply(1L));
        assertEquals((short) 1, func.apply((short) 1));
    }

}
