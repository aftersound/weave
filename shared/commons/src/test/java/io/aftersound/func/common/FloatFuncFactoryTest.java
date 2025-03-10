package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FloatFuncFactoryTest {

    private static final FuncFactory FUNC_FACTORY = new FloatFuncFactory();

    @Test
    public void getFuncDescriptors() {
        assertNotNull(FUNC_FACTORY.getFuncDescriptors());
        assertEquals(9, FUNC_FACTORY.getFuncDescriptors().size());
    }

    @Test
    public void createNull() {
        assertNull(FUNC_FACTORY.create("UNSUPPORTED()"));
    }

    @Test
    public void literalFunc() {
        Func<Object, Float> func = FUNC_FACTORY.create("FLOAT(10.0d)");

        assertEquals(10.0f, func.apply(null), 0.0f);

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT(abc)")
        );
    }

    @Test
    public void eqFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:EQ(10.0)");
        assertTrue(func.apply(10f));
        assertFalse(func.apply(9f));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:EQ()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:EQ(AAA)")
        );
    }

    @Test
    public void fromFunc() {
        Func<String, Float> fromString = FUNC_FACTORY.create("FLOAT:FROM(String)");

        assertEquals(10.0d, fromString.apply("10.0"), 0.0d);
        assertNull(fromString.apply(null));

        Func<Number, Float> fromNumber = FUNC_FACTORY.create("FLOAT:FROM(Number)");

        assertEquals(10.0d, fromNumber.apply(10d), 0.0d);
        assertEquals(10.0d, fromNumber.apply(10), 0.0d);
        assertEquals(10.0d, fromNumber.apply(10f), 0.0d);
        assertEquals(10.0d, fromNumber.apply((short) 10), 0.0d);
        assertNull(fromNumber.apply(null));

        Func<Integer, Float> fromInteger = FUNC_FACTORY.create("FLOAT:FROM(Integer)");
        assertEquals(10.0d, fromInteger.apply(10), 0.0d);
        assertNull(fromInteger.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:FROM(abc)")
        );
    }

    @Test
    public void listFromFunc() {
        Func<List<String>, List<Float>> listFromListOfString = FUNC_FACTORY.create("LIST<FLOAT>:FROM(String)");

        assertEquals(10.0d, listFromListOfString.apply(List.of("10.0")).get(0), 0.0d);
        assertNull(listFromListOfString.apply(null));

        List<String> strings = new ArrayList<>(List.of("10", "20", "30"));
        strings.add(null);
        List<Float> values = listFromListOfString.apply(strings);
        assertNull(values.get(3));

        Func<List<Number>, List<Float>> listFromListOfNumber = FUNC_FACTORY.create("LIST<FLOAT>:FROM(Number)");

        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10d)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of(10f)).get(0), 0.0d);
        assertEquals(10.0d, listFromListOfNumber.apply(List.of((short) 10)).get(0), 0.0d);
        assertNull(listFromListOfNumber.apply(null));

        List<Number> numbers = new ArrayList<>(List.of(10d, 20f, 30));
        numbers.add(null);
        values = listFromListOfNumber.apply(numbers);
        assertNull(values.get(3));

        Func<List<Integer>, List<Float>> listFromListOfInteger = FUNC_FACTORY.create("LIST<FLOAT>:FROM(Integer)");

        assertEquals(10.0d, listFromListOfInteger.apply(List.of(10)).get(0), 0.0d);
        assertNull(listFromListOfInteger.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<FLOAT>:FROM(abc)")
        );
    }

    @Test
    public void geFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:GE(10.0)");
        assertTrue(func.apply(10f));
        assertTrue(func.apply(11f));
        assertFalse(func.apply(9f));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:GE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:GE(AAA)")
        );
    }

    @Test
    public void gtFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:GT(10.0)");
        assertFalse(func.apply(10f));
        assertTrue(func.apply(11f));
        assertTrue(func.apply(12f));
        assertFalse(func.apply(9f));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:GT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:GT(AAA)")
        );
    }

    @Test
    public void leFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:LE(10.0)");
        assertTrue(func.apply(10f));
        assertFalse(func.apply(11f));
        assertTrue(func.apply(9f));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:LE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:LE(AAA)")
        );
    }

    @Test
    public void ltFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:LT(10.0)");
        assertFalse(func.apply(10f));
        assertFalse(func.apply(11f));
        assertTrue(func.apply(9f));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:LT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:LT(AAA)")
        );
    }

    @Test
    public void withinFunc() {
        Func<Float, Boolean> func = FUNC_FACTORY.create("FLOAT:WITHIN(1,100)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0f));
        assertTrue(func.apply(1f));
        assertTrue(func.apply(50f));
        assertTrue(func.apply(99.99f));
        assertTrue(func.apply(100f));
        assertFalse(func.apply(9999f));

        func = FUNC_FACTORY.create("FLOAT:WITHIN(1,100,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0f));
        assertFalse(func.apply(1f));
        assertTrue(func.apply(50f));
        assertTrue(func.apply(99.99f));
        assertTrue(func.apply(100f));
        assertFalse(func.apply(9999f));

        func = FUNC_FACTORY.create("FLOAT:WITHIN(1,100,I,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0f));
        assertTrue(func.apply(1f));
        assertTrue(func.apply(50f));
        assertTrue(func.apply(99.99f));
        assertFalse(func.apply(100f));
        assertFalse(func.apply(9999f));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:WITHIN(A,100)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:WITHIN(1,B)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:WITHIN(1,100,A)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("FLOAT:WITHIN(1,100,I,A)")
        );
    }

}