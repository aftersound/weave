package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void hashFuncs() {
        assertEquals("B94D27B9934D3E08A52E52D7DA7DABFAC484EFE37A5380EE9088F7ACE2EFCDE9", masterFuncFactory.create("HASH(SHA-256,String,String)").apply("hello world"));
        assertEquals("B94D27B9934D3E08A52E52D7DA7DABFAC484EFE37A5380EE9088F7ACE2EFCDE9", masterFuncFactory.create("CHAIN(HASH(SHA-256,String,ByteArray),HEX:STRING())").apply("hello world"));
        assertEquals("B94D27B9934D3E08A52E52D7DA7DABFAC484EFE37A5380EE9088F7ACE2EFCDE9", masterFuncFactory.create("HASH(SHA-256,ByteArray,String)").apply("hello world".getBytes(StandardCharsets.UTF_8)));
        assertEquals("B94D27B9934D3E08A52E52D7DA7DABFAC484EFE37A5380EE9088F7ACE2EFCDE9", masterFuncFactory.create("CHAIN(HASH(SHA-256),HEX:STRING())").apply("hello world".getBytes(StandardCharsets.UTF_8)));
        assertEquals("B94D27B9934D3E08A52E52D7DA7DABFAC484EFE37A5380EE9088F7ACE2EFCDE9", masterFuncFactory.create("CHAIN(HASH(SHA-256,ByteArray,ByteArray),HEX:STRING())").apply("hello world".getBytes(StandardCharsets.UTF_8)));
    }

}
