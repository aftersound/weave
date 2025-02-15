package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoubleFuncFactoryTest {

    private static final FuncFactory FUNC_FACTORY = new DoubleFuncFactory();

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
    public void literalFunc() {
        Func<Object, Double> func = FUNC_FACTORY.create("DOUBLE(10.0d)");

        assertEquals(10.0d, func.apply(null), 0.0d);

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE(abc)")
        );
    }

    @Test
    public void eqFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:EQ(10.0)");
        assertTrue(func.apply(10d));
        assertFalse(func.apply(9d));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:EQ()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:EQ(AAA)")
        );
    }

    @Test
    public void fromFunc() {
        Func<String, Double> fromString = FUNC_FACTORY.create("DOUBLE:FROM(String)");

        assertEquals(10.0d, fromString.apply("10.0"), 0.0d);
        assertNull(fromString.apply(null));

        Func<Number, Double> fromNumber = FUNC_FACTORY.create("DOUBLE:FROM(Number)");

        assertEquals(10.0d, fromNumber.apply(10d), 0.0d);
        assertEquals(10.0d, fromNumber.apply(10), 0.0d);
        assertEquals(10.0d, fromNumber.apply(10f), 0.0d);
        assertEquals(10.0d, fromNumber.apply((short) 10), 0.0d);
        assertNull(fromNumber.apply(null));

        Func<Integer, Double> fromInteger = FUNC_FACTORY.create("DOUBLE:FROM(Integer)");
        assertEquals(10.0d, fromInteger.apply(10), 0.0d);
        assertNull(fromInteger.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:FROM(abc)")
        );
    }

    @Test
    public void listFromFunc() {
        Func<List<String>, List<Double>> listFromListOfString = FUNC_FACTORY.create("LIST<DOUBLE>:FROM(String)");

        assertEquals(10.0d, listFromListOfString.apply(List.of("10.0")).get(0), 0.0d);
        assertNull(listFromListOfString.apply(null));

        List<String> strings = new ArrayList<>(List.of("10", "20", "30"));
        strings.add(null);
        List<Double> values = listFromListOfString.apply(strings);
        assertNull(values.get(3));

        Func<List<Number>, List<Double>> listFromListOfNumber = FUNC_FACTORY.create("LIST<DOUBLE>:FROM(Number)");

        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10d)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10f)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of((short) 10)).get(0), 0.0d);
        assertNull(listFromListOfNumber.apply(null));

        List<Number> numbers = new ArrayList<>(List.of(10d, 20f, 30));
        numbers.add(null);
        values = listFromListOfNumber.apply(numbers);
        assertNull(values.get(3));

        Func<List<Integer>, List<Double>> listFromListOfInteger = FUNC_FACTORY.create("LIST<DOUBLE>:FROM(Integer)");

        assertEquals(10.0d, listFromListOfInteger.apply(List.of(10)).get(0), 0.0d);
        assertNull(listFromListOfInteger.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<DOUBLE>:FROM(abc)")
        );
    }

    @Test
    public void geFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:GE(10.0)");
        assertTrue(func.apply(10d));
        assertTrue(func.apply(11d));
        assertFalse(func.apply(9d));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:GE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:GE(AAA)")
        );
    }

    @Test
    public void gtFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:GT(10.0)");
        assertFalse(func.apply(10d));
        assertTrue(func.apply(11d));
        assertTrue(func.apply(12d));
        assertFalse(func.apply(9d));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:GT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:GT(AAA)")
        );
    }

    @Test
    public void leFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:LE(10.0)");
        assertTrue(func.apply(10d));
        assertFalse(func.apply(11d));
        assertTrue(func.apply(9d));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:LE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:LE(AAA)")
        );
    }

    @Test
    public void ltFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:LT(10.0)");
        assertFalse(func.apply(10d));
        assertFalse(func.apply(11d));
        assertTrue(func.apply(9d));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:LT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:LT(AAA)")
        );
    }

    @Test
    public void withinFunc() {
        Func<Double, Boolean> func = FUNC_FACTORY.create("DOUBLE:WITHIN(1,100)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0d));
        assertTrue(func.apply(1d));
        assertTrue(func.apply(50d));
        assertTrue(func.apply(99.99d));
        assertTrue(func.apply(100d));
        assertFalse(func.apply(9999d));

        func = FUNC_FACTORY.create("DOUBLE:WITHIN(1,100,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0d));
        assertFalse(func.apply(1d));
        assertTrue(func.apply(50d));
        assertTrue(func.apply(99.99d));
        assertTrue(func.apply(100d));
        assertFalse(func.apply(9999d));

        func = FUNC_FACTORY.create("DOUBLE:WITHIN(1,100,I,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0d));
        assertTrue(func.apply(1d));
        assertTrue(func.apply(50d));
        assertTrue(func.apply(99.99d));
        assertFalse(func.apply(100d));
        assertFalse(func.apply(9999d));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:WITHIN(A,100)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:WITHIN(1,B)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:WITHIN(1,100,A)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("DOUBLE:WITHIN(1,100,I,A)")
        );
    }

}