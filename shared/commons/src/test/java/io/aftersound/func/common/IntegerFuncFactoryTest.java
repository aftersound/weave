package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegerFuncFactoryTest {


    private static final FuncFactory FUNC_FACTORY = new IntegerFuncFactory();

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
    public void valueFunc() {
        Func<Object, Integer> func = FUNC_FACTORY.create("INTEGER(10)");

        assertEquals(10, func.apply(null));
        assertEquals(10, func.apply("9"));

        func = FUNC_FACTORY.create("INT(10)");

        assertEquals(10, func.apply(null));
        assertEquals(10, func.apply("9"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER(abc)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INT(abc)")
        );
    }

    @Test
    public void bitwiseAndFunc() {
        Func<Integer, Integer> func = FUNC_FACTORY.create("INTEGER:BAND(3)");
        assertEquals(0b0010, func.apply(0b1010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("INT:BAND(4)");
        assertEquals(0b0000, func.apply(0b1010));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseNotFunc() {
        Func<Integer, Integer> func = FUNC_FACTORY.create("INTEGER:BNOT()");
        assertEquals(0b11111111111111111111111111110101, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("INT:BNOT()");
        assertEquals(0b11111111111111111111111111110101, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseOrFunc() {
        Func<Integer, Integer> func = FUNC_FACTORY.create("INTEGER:BOR(3)");
        assertEquals(0b00000000000000000000000000001011, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("INT:BOR(3)");
        assertEquals(0b00000000000000000000000000001011, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));
    }

    @Test
    public void bitwiseXorFunc() {
        Func<Integer, Integer> func = FUNC_FACTORY.create("INTEGER:BXOR(3)");
        assertEquals(0b00000000000000000000000000001001, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));

        func = FUNC_FACTORY.create("INT:BXOR(3)");
        assertEquals(0b00000000000000000000000000001001, func.apply(0b00000000000000000000000000001010));
        assertNull(func.apply(null));
    }

    @Test
    public void eqFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:EQ(10)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:EQ(10)");
        assertTrue(func.apply(0b1010));
        assertFalse(func.apply(1));
        assertFalse(func.apply(null));

        func = FUNC_FACTORY.create("INT:EQ(10)");
        assertTrue(func.apply(0b1010));
        assertFalse(func.apply(1));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:EQ()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:EQ(abc)")
        );
    }

    @Test
    public void fromFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:FROM(String)"));

        Func<String, Integer> fromString = FUNC_FACTORY.create("INTEGER:FROM(String)");

        assertEquals(10, fromString.apply("10"));
        assertNull(fromString.apply(null));

        Func<Number, Integer> fromNumber = FUNC_FACTORY.create("INTEGER:FROM(Number)");

        assertEquals(10, fromNumber.apply(10d));
        assertEquals(10, fromNumber.apply(10));
        assertEquals(10, fromNumber.apply(10f));
        assertEquals(10, fromNumber.apply((short) 10));
        assertNull(fromNumber.apply(null));

        Func<Double, Integer> fromDouble = FUNC_FACTORY.create("INTEGER:FROM(Double)");
        assertEquals(10, fromDouble.apply(10.01d));
        assertNull(fromDouble.apply(null));

        Func<Object, Integer> fromVarious = FUNC_FACTORY.create("INTEGER:FROM()");

        assertEquals(10, fromVarious.apply(10d));
        assertEquals(10, fromVarious.apply(10));
        assertEquals(10, fromVarious.apply(10f));
        assertEquals(10, fromVarious.apply((short) 10));
        assertEquals(10, fromVarious.apply("10"));
        assertNull(fromVarious.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:FROM(abc)")
        );
    }

    @Test
    public void geFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:GE(10)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:GE(10)");
        assertTrue(func.apply(10));
        assertTrue(func.apply(11));
        assertFalse(func.apply(9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:GE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:GE(AAA)")
        );
    }

    @Test
    public void gtFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:GT(10)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:GT(10)");
        assertFalse(func.apply(10));
        assertTrue(func.apply(11));
        assertTrue(func.apply(12));
        assertFalse(func.apply(9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:GT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:GT(AAA)")
        );
    }

    @Test
    public void leFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:LE(10)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:LE(10)");
        assertTrue(func.apply(10));
        assertFalse(func.apply(11));
        assertTrue(func.apply(9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:LE()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:LE(AAA)")
        );
    }

    @Test
    public void listFromFunc() {
        assertNotNull(FUNC_FACTORY.create("LIST<INT>:FROM(String)"));

        Func<List<String>, List<Integer>> listFromListOfString = FUNC_FACTORY.create("LIST<INTEGER>:FROM(String)");

        assertNull(listFromListOfString.apply(null));

        List<Integer> values = listFromListOfString.apply(List.of("10", "11"));
        assertEquals(10, values.get(0));
        assertEquals(11, values.get(1));

        List<String> source = new ArrayList<>();
        source.add("10");source.add("11");
        source.add(null);

        values = listFromListOfString.apply(source);
        assertEquals(10, values.get(0));
        assertEquals(11, values.get(1));
        assertNull(values.get(2));

        Func<List<Number>, List<Integer>> listFromListOfNumber = FUNC_FACTORY.create("LIST<INTEGER>:FROM(Number)");

        assertNull(listFromListOfNumber.apply(null));

        List<Number> numbers = new ArrayList<>();
        numbers.add(10f);
        numbers.add(11d);
        numbers.add(null);

        values = listFromListOfNumber.apply(numbers);
        assertEquals(10, values.get(0));
        assertEquals(11, values.get(1));
        assertNull(values.get(2));

        assertNotNull(FUNC_FACTORY.create("LIST<INTEGER>:FROM(Double)"));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<INTEGER>:FROM()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("LIST<INTEGER>:FROM(AAA)")
        );
    }

    @Test
    public void ltFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:LT(10)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:LT(10)");
        assertFalse(func.apply(10));
        assertFalse(func.apply(11));
        assertTrue(func.apply(9));
        assertFalse(func.apply(null));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:LT()")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:LT(AAA)")
        );
    }



    @Test
    public void withinFunc() {
        assertNotNull(FUNC_FACTORY.create("INT:WITHIN(1,100)"));

        Func<Integer, Boolean> func = FUNC_FACTORY.create("INTEGER:WITHIN(1,100)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0));
        assertTrue(func.apply(1));
        assertTrue(func.apply(50));
        assertTrue(func.apply(99));
        assertTrue(func.apply(100));
        assertFalse(func.apply(9999));

        func = FUNC_FACTORY.create("INTEGER:WITHIN(1,100,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0));
        assertFalse(func.apply(1));
        assertTrue(func.apply(50));
        assertTrue(func.apply(99));
        assertTrue(func.apply(100));
        assertFalse(func.apply(9999));

        func = FUNC_FACTORY.create("INTEGER:WITHIN(1,100,I,E)");
        assertFalse(func.apply(null));
        assertFalse(func.apply(0));
        assertTrue(func.apply(1));
        assertTrue(func.apply(50));
        assertTrue(func.apply(99));
        assertFalse(func.apply(100));
        assertFalse(func.apply(9999));

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:WITHIN(A,100)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:WITHIN(1,B)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:WITHIN(1,100,A)")
        );

        assertThrows(
                CreationException.class,
                () -> FUNC_FACTORY.create("INTEGER:WITHIN(1,100,I,A)")
        );
    }

}