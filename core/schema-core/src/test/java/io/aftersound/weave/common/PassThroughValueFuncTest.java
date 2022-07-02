package io.aftersound.weave.common;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class PassThroughValueFuncTest {

    @Test
    public void apply() {
        ValueFunc<Object, Object> f = PassThroughValueFunc.INSTANCE;

        // ensure same object because internal delegate of FailSafeValueFunc is PassThroughValueFunc
        Object v;
        v = "Hello";
        assertTrue(v == f.apply(v));
        v = new HashMap<>();
        assertTrue(v == f.apply(v));
    }

}