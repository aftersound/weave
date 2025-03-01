package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShortFuncFactoryTest {


    private static final FuncFactory FUNC_FACTORY = new ShortFuncFactory();

    @Test
    public void getFuncDescriptors() {
        assertNotNull(FUNC_FACTORY.getFuncDescriptors());
        assertEquals(3, FUNC_FACTORY.getFuncDescriptors().size());
    }

    @Test
    public void createNull() {
        assertNull(FUNC_FACTORY.create("UNSUPPORTED()"));
    }

    @Test
    public void valueFunc() {
        Func<Object, Short> func = FUNC_FACTORY.create("SHORT(10)");

        assertEquals((short) 10, func.apply(null));
        assertEquals((short) 10, func.apply("9"));

        func = FUNC_FACTORY.create("SHORT(10)");

        assertEquals((short) 10, func.apply(null));
        assertEquals((short) 10, func.apply("9"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT(abc)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT(abc)")
        );
    }

    @Test
    public void bitwiseAndFunc() {
        Func<Short, Short> func = FUNC_FACTORY.create("SHORT:BAND(3)");
        assertEquals((short) 0b0010, func.apply((short) 0b1010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("SHORT:BAND(4)");
        assertEquals((short) 0b0000, func.apply((short) 0b1010));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseNotFunc() {
        Func<Short, Short> func = FUNC_FACTORY.create("SHORT:BNOT()");
        assertEquals((short) 0xFFF5, func.apply((short) 0x000A));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseOrFunc() {
        Func<Short, Short> func = FUNC_FACTORY.create("SHORT:BOR(3)");
        assertEquals((short) 0b00001011, func.apply((short) 0b00001010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("SHORT:BOR(3)");
        assertEquals((short) 0b000001011, func.apply((short) 0b00001010));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseXorFunc() {
        Func<Short, Short> func = FUNC_FACTORY.create("SHORT:BXOR(3)");
        assertEquals((short) 0b00001001, func.apply((short) 0b00001010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("SHORT:BXOR(3)");
        assertEquals((short) 0b00001001, func.apply((short) 0b000001010));
        assertNull(func.apply(null));
    }

    @Test
    public void eqFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:EQ(10)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:EQ(10)");
        assertTrue(func.apply((short) 0b1010));
        assertFalse(func.apply((short) 1));
        assertFalse(func.apply(null));

        func = FUNC_FACTORY.create("SHORT:EQ(10)");
        assertTrue(func.apply((short) 0b1010));
        assertFalse(func.apply((short) 1));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:EQ()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:EQ(abc)")
        );
    }

    @Test
    public void fromFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:FROM(String)"));

        Func<String, Short> fromString = FUNC_FACTORY.create("SHORT:FROM(String)");

        assertEquals((short) 10, fromString.apply("10"));
        assertNull(fromString.apply(null));

        Func<Number, Short> fromNumber = FUNC_FACTORY.create("SHORT:FROM(Number)");

        assertEquals((short) 10, fromNumber.apply(10d));
        assertEquals((short) 10, fromNumber.apply(10));
        assertEquals((short) 10, fromNumber.apply(10f));
        assertEquals((short) 10, fromNumber.apply((short) 10));
        assertNull(fromNumber.apply(null));

        Func<Double, Short> fromDouble = FUNC_FACTORY.create("SHORT:FROM(Double)");
        assertEquals((short) 10, fromDouble.apply(10.01d));
        assertNull(fromDouble.apply(null));

        Func<Object, Short> fromVarious = FUNC_FACTORY.create("SHORT:FROM()");

        assertEquals((short) 10, fromVarious.apply(10d));
        assertEquals((short) 10, fromVarious.apply(10));
        assertEquals((short) 10, fromVarious.apply(10f));
        assertEquals((short) 10, fromVarious.apply((short) 10));
        assertEquals((short) 10, fromVarious.apply("10"));
        assertNull(fromVarious.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:FROM(abc)")
        );
    }

    @Test
    public void geFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:GE(10)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:GE(10)");
        assertTrue(func.apply((short) 10));
        assertTrue(func.apply((short) 11));
        assertFalse(func.apply((short) 9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:GE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:GE(AAA)")
        );
    }

    @Test
    public void gtFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:GT(10)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:GT(10)");
        assertFalse(func.apply((short) 10));
        assertTrue(func.apply((short) 11));
        assertTrue(func.apply((short) 12));
        assertFalse(func.apply((short) 9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:GT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:GT(AAA)")
        );
    }

    @Test
    public void leFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:LE(10)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:LE(10)");
        assertTrue(func.apply((short) 10));
        assertFalse(func.apply((short) 11));
        assertTrue(func.apply((short) 9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:LE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:LE(AAA)")
        );
    }

    @Test
    public void listFromFunc() {
        assertNotNull(FUNC_FACTORY.create("LIST<SHORT>:FROM(String)"));

        Func<List<String>, List<Short>> listFromListOfString = FUNC_FACTORY.create("LIST<SHORT>:FROM(String)");

        assertNull(listFromListOfString.apply(null));

        List<Short> values = listFromListOfString.apply(List.of("10", "11"));
        assertEquals((short) 10, values.get(0));
        assertEquals((short) 11, values.get(1));

        List<String> source = new ArrayList<>();
        source.add("10");source.add("11");
        source.add(null);

        values = listFromListOfString.apply(source);
        assertEquals((short) 10, values.get(0));
        assertEquals((short) 11, values.get(1));
        assertNull(values.get(2));

        Func<List<Number>, List<Short>> listFromListOfNumber = FUNC_FACTORY.create("LIST<SHORT>:FROM(Number)");

        assertNull(listFromListOfNumber.apply(null));

        List<Number> numbers = new ArrayList<>();
        numbers.add(10f);
        numbers.add(11d);
        numbers.add(null);

        values = listFromListOfNumber.apply(numbers);
        assertEquals((short) 10, values.get(0));
        assertEquals((short) 11, values.get(1));
        assertNull(values.get(2));

        assertNotNull(FUNC_FACTORY.create("LIST<SHORT>:FROM(Double)"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<SHORT>:FROM()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<SHORT>:FROM(AAA)")
        );
    }

    @Test
    public void ltFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:LT(10)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:LT(10)");
        assertFalse(func.apply((short) 10));
        assertFalse(func.apply((short) 11));
        assertTrue(func.apply((short) 9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:LT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:LT(AAA)")
        );
    }



    @Test
    public void withinFunc() {
        assertNotNull(FUNC_FACTORY.create("SHORT:WITHIN(1,100)"));

        Func<Short, Boolean> func = FUNC_FACTORY.create("SHORT:WITHIN(1,100)");
        assertFalse(func.apply(null));
        assertFalse(func.apply((short) 0));
        assertTrue(func.apply((short) 1));
        assertTrue(func.apply((short) 50));
        assertTrue(func.apply((short) 99));
        assertTrue(func.apply((short) 100));
        assertFalse(func.apply((short) 9999));

        func = FUNC_FACTORY.create("SHORT:WITHIN(1,100,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply((short) 0));
        assertFalse(func.apply((short) 1));
        assertTrue(func.apply((short) 50));
        assertTrue(func.apply((short) 99));
        assertTrue(func.apply((short) 100));
        assertFalse(func.apply((short) 9999));

        func = FUNC_FACTORY.create("SHORT:WITHIN(1,100,I,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply((short) 0));
        assertTrue(func.apply((short) 1));
        assertTrue(func.apply((short) 50));
        assertTrue(func.apply((short) 99));
        assertFalse(func.apply((short) 100));
        assertFalse(func.apply((short) 9999));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:WITHIN(A,100)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:WITHIN(1,B)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:WITHIN(1,100,A)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("SHORT:WITHIN(1,100,I,A)")
        );
    }

}