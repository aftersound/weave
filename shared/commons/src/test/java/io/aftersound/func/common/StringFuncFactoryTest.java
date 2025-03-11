package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.MapBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class StringFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void getFuncDescriptors() {
        assertFalse(new StringFuncFactory().getFuncDescriptors().isEmpty());
    }

    @Test
    public void test() {
        assertEquals("weave", masterFuncFactory.create("STR(weave)").apply(null));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("STR:CONTAINS(weave)").apply("weaver"));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("STR:CONTAINS(weave)").apply(null));
        assertEquals("NANOSECONDS", masterFuncFactory.create("STR:FROM()").apply(TimeUnit.NANOSECONDS));
        assertEquals("-999.99", masterFuncFactory.create("STR:FROM()").apply(-999.99d));
        assertEquals("99.99", masterFuncFactory.create("STR:FROM()").apply(99.99f));
        assertEquals("99", masterFuncFactory.create("STR:FROM()").apply(99));
        assertEquals("true", masterFuncFactory.create("STR:FROM()").apply(true));
        assertNull(masterFuncFactory.create("STR:FROM()").apply(null));

        assertEquals("hello", masterFuncFactory.create("CHAIN(STR:ENCODE(),STR:DECODE())").apply("hello"));
        assertInstanceOf(String.class, masterFuncFactory.create("STR:RANDOM(10)").apply(null));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("STR:MATCH(BASE64|Tmlrb2xhKC4qKQ==)").apply("Nikola Tesla"));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("STRING:MATCH(BASE64|Tmlrb2xhKC4qKQ==)").apply("Thomas Edison"));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("STR:START_WITH(Nikola)").apply("Nikola Tesla"));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("STR:START_WITH(Nikola)").apply("Thomas Edison"));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("STR:END_WITH(Tesla)").apply("Nikola Tesla"));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("STR:END_WITH(Tesla)").apply("Thomas Edison"));

        assertInstanceOf(List.class, masterFuncFactory.create("STR:READ_LINES()").apply("hello\nworld"));

        // used in FILTER
        assertNotNull(masterFuncFactory.create("FILTER(CHAIN(MAP:GET(name),STR:MATCH(BASE64|Tmlrb2xhKC4qKQ==)))").apply(MapBuilder.<String, Object>hashMap().put("name", "Nikola Tesla").build()));
        assertNull(masterFuncFactory.create("FILTER(CHAIN(MAP:GET(name),STR:MATCH(BASE64|Tmlrb2xhKC4qKQ==)))").apply(MapBuilder.<String, Object>hashMap().put("name", "Thomas Edison").build()));
    }

    @Test
    public void joinFunc() {
        assertEquals("a,b,c", masterFuncFactory.create("STRING:JOIN(URL|%2C)").apply(List.of("a","b","c")));
    }

    @Test
    public void listFromFunc() {
        List<String> values = (List<String>) masterFuncFactory.create("LIST<STRING>:FROM()").apply(Arrays.asList("1","2"));
        assertEquals(2, values.size());
        assertEquals("1", values.get(0));
        assertEquals("2", values.get(1));

        values = (List<String>) masterFuncFactory.create("LIST<STRING>:FROM()").apply(Arrays.asList(111.1f,222.2f));
        assertEquals(2, values.size());
        assertEquals("111.1", values.get(0));
        assertEquals("222.2", values.get(1));
    }
}
