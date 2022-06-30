package io.aftersound.weave.common;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class FailSafeValueFuncTest {

    @BeforeClass
    public static void setup() throws Exception {
        MasterValueFuncFactory.init(T1ValueFuncFactory.class.getName(), T2ValueFuncFactory.class.getName());
    }

    @Test
    public void appy() {
        ValueFunc<Object, Object> f = new FailSafeValueFunc("T1:LOWER_CASE");
        assertEquals("hello", f.apply("HELLO"));
        Integer i = Integer.valueOf(100);
        assertEquals(Integer.class.getSimpleName() + "@" + i.hashCode(), f.apply(i));
    }

}