package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URLFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void uRLDecodeFunc() {
        assertEquals(", )", masterFuncFactory.create("URL:DECODE()").apply("%2C%20%29"));
    }

    @Test
    public void uRLEncodeFunc() {
        assertEquals("%2C+%29", masterFuncFactory.create("URL:ENCODE()").apply(", )"));
    }

}
