package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LongFuncFactoryTest {

    private static final FuncFactory FUNC_FACTORY = new LongFuncFactory();

    @Test
    public void getFuncDescriptors() {
        assertNotNull(FUNC_FACTORY.getFuncDescriptors());
        assertEquals(13, FUNC_FACTORY.getFuncDescriptors().size());
    }

    @Test
    public void createNull() {
        assertNull(FUNC_FACTORY.create("UNSUPPORTED()"));
    }

    @Test
    public void literalFunc() {
        Func<Object, Long> func = FUNC_FACTORY.create("LONG(10)");

        assertEquals(10L, func.apply(null));
        assertEquals(10L, func.apply("9"));

        func = FUNC_FACTORY.create("LONG(10)");

        assertEquals(10L, func.apply(null));
        assertEquals(10L, func.apply("9"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG(abc)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG(abc)")
        );
    }

    @Test
    public void bitwiseAndFunc() {
        Func<Long, Long> func = FUNC_FACTORY.create("LONG:BAND(3)");
        assertEquals(0b0010L, func.apply(0b1010L));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("LONG:BAND(4)");
        assertEquals(0b0000L, func.apply(0b1010L));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseNotFunc() {
        Func<Long, Long> func = FUNC_FACTORY.create("LONG:BNOT()");
        assertEquals(0xFFFFFFFFFFFFFFF5L, func.apply(0x000000000000000AL));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseOrFunc() {
        Func<Long, Long> func = FUNC_FACTORY.create("LONG:BOR(3)");
        assertEquals(0b00000000000000000000000000001011, func.apply(0b00000000000000000000000000001010L));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("LONG:BOR(3)");
        assertEquals(0b00000000000000000000000000001011, func.apply(0b00000000000000000000000000001010L));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseXorFunc() {
        Func<Long, Long> func = FUNC_FACTORY.create("LONG:BXOR(3)");
        assertEquals(0b00000000000000000000000000001001L, func.apply(0b00000000000000000000000000001010L));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("LONG:BXOR(3)");
        assertEquals(0b00000000000000000000000000001001L, func.apply(0b00000000000000000000000000001010L));
        assertNull(func.apply(null));
    }

    @Test
    public void eqFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:EQ(10)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:EQ(10)");
        assertTrue(func.apply(0b1010L));
        assertFalse(func.apply(1L));
        assertFalse(func.apply(null));

        func = FUNC_FACTORY.create("LONG:EQ(10)");
        assertTrue(func.apply(0b1010L));
        assertFalse(func.apply(1L));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:EQ()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:EQ(abc)")
        );
    }

    @Test
    public void fromFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:FROM(String)"));

        Func<String, Long> fromString = FUNC_FACTORY.create("LONG:FROM(String)");

        assertEquals(10, fromString.apply("10"));
        assertNull(fromString.apply(null));

        Func<Number, Long> fromNumber = FUNC_FACTORY.create("LONG:FROM(Number)");

        assertEquals(10L, fromNumber.apply(10d));
        assertEquals(10L, fromNumber.apply(10));
        assertEquals(10L, fromNumber.apply(10f));
        assertEquals(10L, fromNumber.apply((short) 10));
        assertNull(fromNumber.apply(null));

        Func<Double, Long> fromDouble = FUNC_FACTORY.create("LONG:FROM(Double)");
        assertEquals(10L, fromDouble.apply(10.01d));
        assertNull(fromDouble.apply(null));

        Func<Object, Long> fromVarious = FUNC_FACTORY.create("LONG:FROM()");

        assertEquals(10L, fromVarious.apply(10d));
        assertEquals(10L, fromVarious.apply(10));
        assertEquals(10L, fromVarious.apply(10f));
        assertEquals(10L, fromVarious.apply((short) 10));
        assertEquals(10L, fromVarious.apply("10"));
        assertNull(fromVarious.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:FROM(abc)")
        );
    }

    @Test
    public void geFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:GE(10)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:GE(10)");
        assertTrue(func.apply(10L));
        assertTrue(func.apply(11L));
        assertFalse(func.apply(9L));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:GE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:GE(AAA)")
        );
    }

    @Test
    public void gtFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:GT(10)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:GT(10)");
        assertFalse(func.apply(10L));
        assertTrue(func.apply(11L));
        assertTrue(func.apply(12L));
        assertFalse(func.apply(9L));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:GT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:GT(AAA)")
        );
    }

    @Test
    public void leFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:LE(10)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:LE(10)");
        assertTrue(func.apply(10L));
        assertFalse(func.apply(11L));
        assertTrue(func.apply(9L));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:LE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:LE(AAA)")
        );
    }

    @Test
    public void listFromFunc() {
        assertNotNull(FUNC_FACTORY.create("LIST<LONG>:FROM(String)"));

        Func<List<String>, List<Long>> listFromListOfString = FUNC_FACTORY.create("LIST<LONG>:FROM(String)");

        assertNull(listFromListOfString.apply(null));

        List<Long> values = listFromListOfString.apply(List.of("10", "11"));
        assertEquals(10L, values.get(0));
        assertEquals(11L, values.get(1));

        List<String> source = new ArrayList<>();
        source.add("10");source.add("11");
        source.add(null);

        values = listFromListOfString.apply(source);
        assertEquals(10L, values.get(0));
        assertEquals(11L, values.get(1));
        assertNull(values.get(2));

        Func<List<Number>, List<Long>> listFromListOfNumber = FUNC_FACTORY.create("LIST<LONG>:FROM(Number)");

        assertNull(listFromListOfNumber.apply(null));

        List<Number> numbers = new ArrayList<>();
        numbers.add(10f);
        numbers.add(11d);
        numbers.add(null);

        values = listFromListOfNumber.apply(numbers);
        assertEquals(10L, values.get(0));
        assertEquals(11L, values.get(1));
        assertNull(values.get(2));

        assertNotNull(FUNC_FACTORY.create("LIST<LONG>:FROM(Double)"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<LONG>:FROM()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<LONG>:FROM(AAA)")
        );
    }

    @Test
    public void ltFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:LT(10)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:LT(10)");
        assertFalse(func.apply(10L));
        assertFalse(func.apply(11L));
        assertTrue(func.apply(9L));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:LT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:LT(AAA)")
        );
    }

    @Test
    public void withinFunc() {
        assertNotNull(FUNC_FACTORY.create("LONG:WITHIN(1,100)"));

        Func<Long, Boolean> func = FUNC_FACTORY.create("LONG:WITHIN(1,100)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0L));
        assertTrue(func.apply(1L));
        assertTrue(func.apply(50L));
        assertTrue(func.apply(99L));
        assertTrue(func.apply(100L));
        assertFalse(func.apply(9999L));

        func = FUNC_FACTORY.create("LONG:WITHIN(1,100,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0L));
        assertFalse(func.apply(1L));
        assertTrue(func.apply(50L));
        assertTrue(func.apply(99L));
        assertTrue(func.apply(100L));
        assertFalse(func.apply(9999L));

        func = FUNC_FACTORY.create("LONG:WITHIN(1,100,I,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0L));
        assertTrue(func.apply(1L));
        assertTrue(func.apply(50L));
        assertTrue(func.apply(99L));
        assertFalse(func.apply(100L));
        assertFalse(func.apply(9999L));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:WITHIN(A,100)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:WITHIN(1,B)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:WITHIN(1,100,A)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LONG:WITHIN(1,100,I,A)")
        );
    }

}