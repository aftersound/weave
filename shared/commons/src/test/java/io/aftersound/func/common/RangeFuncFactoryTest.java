package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.Range;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class RangeFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void rangeFunc() {
        Range<Date> r1 = (Range<Date>) masterFuncFactory.create("RANGE:FROM(Date(yyyyMMdd))").apply("[20210309,20210316]");
        assertNotNull(r1);
        assertNotNull(r1.getLower());
        assertTrue(r1.isLowerInclusive());
        assertNotNull(r1.getUpper());
        assertTrue(r1.isUpperInclusive());

        Range<Double> r2 = (Range<Double>) masterFuncFactory.create("RANGE:FROM(Double)").apply("(0.00,9.99)");
        assertNotNull(r2);
        assertEquals(0d, r2.getLower().doubleValue(), 0.0d);
        assertFalse(r2.isLowerInclusive());
        assertEquals(9.99d, r2.getUpper().doubleValue(), 0.0d);
        assertFalse(r2.isUpperInclusive());

        Range<Float> r3 = (Range<Float>) masterFuncFactory.create("RANGE:FROM(Float)").apply("(0.00,9.99]");
        assertNotNull(r3);
        assertEquals(0f, r3.getLower(), 0.0f);
        assertFalse(r3.isLowerInclusive());
        assertEquals(9.99f, r3.getUpper(), 0.0f);
        assertTrue(r3.isUpperInclusive());

        Range<Integer> r4 = (Range<Integer>) masterFuncFactory.create("RANGE:FROM(Integer)").apply("(0,9]");
        assertNotNull(r4);
        assertEquals(0, r4.getLower().intValue());
        assertFalse(r4.isLowerInclusive());
        assertEquals(9, r4.getUpper().intValue());
        assertTrue(r4.isUpperInclusive());

        Range<Long> r5 = (Range<Long>) masterFuncFactory.create("RANGE:FROM(Long)").apply("(0,9]");
        assertNotNull(r5);
        assertEquals(0L, r5.getLower().longValue());
        assertFalse(r5.isLowerInclusive());
        assertEquals(9L, r5.getUpper().longValue());
        assertTrue(r5.isUpperInclusive());

        Range<Short> r6 = (Range<Short>) masterFuncFactory.create("RANGE:FROM(Short)").apply("(0,9]");
        assertNotNull(r6);
        assertEquals((short) 0, r6.getLower().shortValue());
        assertFalse(r6.isLowerInclusive());
        assertEquals((short) 9, r6.getUpper().shortValue());
        assertTrue(r6.isUpperInclusive());

        Range<String> r7 = (Range<String>) masterFuncFactory.create("RANGE:FROM(String)").apply("(0,9]");
        assertNotNull(r7);
        assertEquals("0", r7.getLower());
        assertFalse(r7.isLowerInclusive());
        assertEquals("9", r7.getUpper());
        assertTrue(r7.isUpperInclusive());

        assertNull(masterFuncFactory.create("RANGE:FROM(Short)").apply(null));
        assertNull(masterFuncFactory.create("RANGE:FROM(Short)").apply(""));
        assertNull(masterFuncFactory.create("RANGE:FROM(Short)").apply("abc"));
    }

}
