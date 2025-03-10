package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HexFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void hexFuncs() {
        assertEquals("68656C6C6F20776F726C64", masterFuncFactory.create("HEX:ENCODE()").apply("hello world".getBytes(StandardCharsets.UTF_8)));
        assertEquals("hello world", masterFuncFactory.create("CHAIN(HEX:DECODE(),STR:DECODE())").apply("68656C6C6F20776F726C64"));
    }

}
