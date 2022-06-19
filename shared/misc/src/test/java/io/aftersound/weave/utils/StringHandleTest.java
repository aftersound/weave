package io.aftersound.weave.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringHandleTest {

    @Test
    public void testStringHandle() {
        assertEquals(System.getProperty("java.io.tmpdir"), StringHandle.of("${java.io.tmpdir}").value());
        assertEquals(
                System.getProperty("java.io.tmpdir") + "/StringHandleTest",
                StringHandle.of("${java.io.tmpdir}/${test}", MapBuilder.hashMap().kv("test", "StringHandleTest").build()).value()
        );
    }

}