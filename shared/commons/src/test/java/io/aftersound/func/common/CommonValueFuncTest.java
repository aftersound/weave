package io.aftersound.func.common;

public class CommonValueFuncTest {

//    @BeforeClass
//    public static void setup() throws Exception {
//        MasterValueFuncFactory.init(CommonValueFuncFactory.class.getName());
//    }
//
//    @Test
//    public void defaultValueFunc() {
//        assertEquals(Boolean.FALSE, MasterValueFuncFactory.create("DEFAULT(Boolean,true)").apply(Boolean.FALSE));
//        assertEquals(new Date(1615349888), MasterValueFuncFactory.create("DEFAULT(Date,1615349890)").apply(new Date(1615349888)));
//        assertEquals(88d, MasterValueFuncFactory.create("DEFAULT(Double,-999.99)").apply(88d));
//        assertEquals(88f, MasterValueFuncFactory.create("DEFAULT(Float,-999.99)").apply(88f));
//        assertEquals(Integer.valueOf(66), MasterValueFuncFactory.create("DEFAULT(Integer,88)").apply(66));
//        assertEquals(666666666L, MasterValueFuncFactory.create("DEFAULT(Long,88888888888)").apply(666666666L));
//        assertEquals((short) 66, MasterValueFuncFactory.create("DEFAULT(Short,88)").apply((short)66));
//        assertEquals("C,D", MasterValueFuncFactory.create("DEFAULT(String,A%2CB)").apply("C,D"));
//        assertEquals(TimeUnit.SECONDS, MasterValueFuncFactory.create("DEFAULT(Enum(java.util.concurrent.TimeUnit),MICROSECONDS)").apply(TimeUnit.SECONDS));
//
//        // single value
//        assertEquals(Boolean.TRUE, MasterValueFuncFactory.create("DEFAULT(Boolean,true)").apply(null));
//        assertEquals(new Date(1615349890), MasterValueFuncFactory.create("DEFAULT(Date,1615349890)").apply(null));
//        assertEquals(-999.99d, MasterValueFuncFactory.create("DEFAULT(Double,-999.99)").apply(null));
//        assertEquals(-999.99f, MasterValueFuncFactory.create("DEFAULT(Float,-999.99)").apply(null));
//        assertEquals(88, MasterValueFuncFactory.create("DEFAULT(Integer,88)").apply(null));
//        assertEquals(88888888888L, MasterValueFuncFactory.create("DEFAULT(Long,88888888888)").apply(null));
//        assertEquals((short) 88, MasterValueFuncFactory.create("DEFAULT(Short,88)").apply(null));
//        assertEquals("A,B", MasterValueFuncFactory.create("DEFAULT(String,A%2CB)").apply(null));
//        assertEquals(TimeUnit.MICROSECONDS, MasterValueFuncFactory.create("DEFAULT(Enum(java.util.concurrent.TimeUnit),MICROSECONDS)").apply(null));
//
//        // list
//        List<?> values;
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(BooleanList,true,true,false)").apply(null);
//        assertEquals(3, values.size());
//        assertEquals(Boolean.TRUE, values.get(0));
//        assertEquals(Boolean.TRUE, values.get(1));
//        assertEquals(Boolean.FALSE, values.get(2));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(DateList,1615349890,1615349891)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(new Date(1615349890), values.get(0));
//        assertEquals(new Date(1615349891), values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(DoubleList,888.88,999.99)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(888.88d, values.get(0));
//        assertEquals(999.99d, values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(FloatList,888.88,999.99)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(888.88f, values.get(0));
//        assertEquals(999.99f, values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(IntegerList,88888,99999)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(88888, values.get(0));
//        assertEquals(99999, values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(LongList,88888,99999)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(88888L, values.get(0));
//        assertEquals(99999L, values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(ShortList,888,999)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals((short) 888, values.get(0));
//        assertEquals((short) 999, values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(StringList,A%2CB,CD)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals("A,B", values.get(0));
//        assertEquals("CD", values.get(1));
//
//        values = (List<?>) MasterValueFuncFactory.create("DEFAULT(EnumList(java.util.concurrent.TimeUnit),MICROSECONDS,NANOSECONDS)").apply(null);
//        assertEquals(2, values.size());
//        assertEquals(TimeUnit.MICROSECONDS, values.get(0));
//        assertEquals(TimeUnit.NANOSECONDS, values.get(1));
//
//        // Set
//        Set<?> valueSet;
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(BooleanSet,true,true,false)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(Boolean.TRUE));
//        assertTrue(valueSet.contains(Boolean.FALSE));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(DateSet,1615349890,1615349890)").apply(null);
//        assertEquals(1, valueSet.size());
//        assertTrue(valueSet.contains(new Date(1615349890)));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(DoubleSet,888.88,999.99,999.99)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(888.88d));
//        assertTrue(valueSet.contains(999.99d));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(FloatSet,888.88,999.99,888.88)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(888.88f));
//        assertTrue(valueSet.contains(999.99f));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(IntegerSet,88888,99999,99999)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(88888));
//        assertTrue(valueSet.contains(99999));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(LongSet,88888,99999,88888)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(88888L));
//        assertTrue(valueSet.contains(99999L));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(ShortSet,888,999,888,777)").apply(null);
//        assertEquals(3, valueSet.size());
//        assertTrue(valueSet.contains((short) 777));
//        assertTrue(valueSet.contains((short) 888));
//        assertTrue(valueSet.contains((short) 999));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(StringSet,A%2CB,CD)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains("A,B"));
//        assertTrue(valueSet.contains("CD"));
//
//        valueSet = (Set<?>) MasterValueFuncFactory.create("DEFAULT(EnumSet(java.util.concurrent.TimeUnit),MICROSECONDS,NANOSECONDS,NANOSECONDS)").apply(null);
//        assertEquals(2, valueSet.size());
//        assertTrue(valueSet.contains(TimeUnit.MICROSECONDS));
//        assertTrue(valueSet.contains(TimeUnit.NANOSECONDS));
//    }

}