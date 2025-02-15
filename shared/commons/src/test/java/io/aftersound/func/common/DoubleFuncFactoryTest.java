package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleFuncFactoryTest {

    private static FuncFactory funcFactory = new DoubleFuncFactory();

    @Test
    public void literalFunc() {
        Func<Object, Double> func = funcFactory.create("DOUBLE(10.0d)");

        assertEquals(10.0d, func.apply(null), 0.0d);

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("DOUBLE(abc)")
        );
    }

    @Test
    public void fromFunc() {
        Func<String, Double> fromString = funcFactory.create("DOUBLE:FROM(string)");

        assertEquals(10.0d, fromString.apply("10.0"), 0.0d);

        Func<Number, Double> fromNumber = funcFactory.create("DOUBLE:FROM(number)");

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("DOUBLE(abc)")
        );
    }

}