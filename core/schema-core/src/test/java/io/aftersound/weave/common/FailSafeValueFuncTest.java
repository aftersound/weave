package io.aftersound.weave.common;

import io.aftersound.weave.utils.TextualExprTreeParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class FailSafeValueFuncTest {

    @BeforeClass
    public static void setup() throws Exception {
        MasterValueFuncFactory.init(T1ValueFuncFactory.class.getName(), T2ValueFuncFactory.class.getName());
    }

    @Test
    public void apply() throws Exception {
        ValueFunc<Object, Object> f;

        f = new FailSafeValueFunc(MasterValueFuncFactory.create("T1:LOWER_CASE()"));
        assertEquals("hello", f.apply("HELLO"));
        assertNull(f.apply(Integer.valueOf(100)));

        f = new FailSafeValueFunc("T1:LOWER_CASE()");
        assertEquals("hello", f.apply("HELLO"));
        assertNull(f.apply(Integer.valueOf(100)));

        f = new FailSafeValueFunc(TextualExprTreeParser.parse("T1:LOWER_CASE()"));
        assertEquals("hello", f.apply("HELLO"));
        assertNull(f.apply(Integer.valueOf(100)));

        f = new FailSafeValueFunc("VOID:NOT_SUPPORTED");
        // ensure same object because internal delegate of FailSafeValueFunc is PassThroughValueFunc
        Object v;
        v = "Hello";
        assertTrue(v == f.apply(v));
        v = new HashMap<>();
        assertTrue(v == f.apply(v));
    }

}