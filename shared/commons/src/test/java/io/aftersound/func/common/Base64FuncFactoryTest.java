package io.aftersound.func.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class Base64FuncFactoryTest {
    
    private static MasterFuncFactory  masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void getFuncDescriptors() throws Exception {
        System.out.println(new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .writeValueAsString(new Base64FuncFactory().getFuncDescriptors()));
    }

    @Test
    public void base64DecodeFunc() {
        Object v;

        v = masterFuncFactory.create("BASE64:DECODE(String,String)").apply("d2VhdmU=");
        assertInstanceOf(String.class, v);
        assertEquals("weave", v);

        v = masterFuncFactory.create("BASE64:DECODE(String,Bytes)").apply("d2VhdmU=");
        assertInstanceOf(byte[].class, v);
        assertEquals("weave", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:DECODE(String,ByteBuffer)").apply("d2VhdmU=");
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("weave", new String(((ByteBuffer) v).array()));

        byte[] bytes = "d2VhdmU=".getBytes(StandardCharsets.UTF_8);
        v = masterFuncFactory.create("BASE64:DECODE(Bytes,String)").apply(bytes);
        assertInstanceOf(String.class, v);
        assertEquals("weave", v);

        v = masterFuncFactory.create("BASE64:DECODE()").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("weave", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:DECODE(ByteArray)").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("weave", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:DECODE(Bytes,Bytes)").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("weave", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:DECODE(Bytes,ByteBuffer)").apply(bytes);
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("weave", new String(((ByteBuffer) v).array()));

        ByteBuffer bb = ByteBuffer.wrap("d2VhdmU=".getBytes(StandardCharsets.UTF_8));
        v = masterFuncFactory.create("BASE64:DECODE(ByteBuffer,String)").apply(bb);
        assertInstanceOf(String.class, v);
        assertEquals("weave", v);

        v = masterFuncFactory.create("BASE64:DECODE(ByteBuffer,Bytes)").apply(bb);
        assertInstanceOf(byte[].class, v);
        assertEquals("weave", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:DECODE(ByteBuffer,ByteBuffer)").apply(bb);
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("weave", new String(((ByteBuffer) v).array()));
    }

    @Test
    public void base64EncodeFunc() {
        Object v;

        v = masterFuncFactory.create("BASE64:ENCODE(String,String)").apply("weave");
        assertInstanceOf(String.class, v);
        assertEquals("d2VhdmU=", v);

        v = masterFuncFactory.create("BASE64:ENCODE(String,Bytes)").apply("weave");
        assertInstanceOf(byte[].class, v);
        assertEquals("d2VhdmU=", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:ENCODE(String,ByteBuffer)").apply("weave");
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("d2VhdmU=", new String(((ByteBuffer) v).array()));

        byte[] bytes = "weave".getBytes(StandardCharsets.UTF_8);
        v = masterFuncFactory.create("BASE64:ENCODE(Bytes,String)").apply(bytes);
        assertInstanceOf(String.class, v);
        assertEquals("d2VhdmU=", v);

        v = masterFuncFactory.create("BASE64:ENCODE()").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("d2VhdmU=", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:ENCODE(Bytes)").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("d2VhdmU=", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:ENCODE(Bytes,Bytes)").apply(bytes);
        assertInstanceOf(byte[].class, v);
        assertEquals("d2VhdmU=", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:ENCODE(Bytes,ByteBuffer)").apply(bytes);
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("d2VhdmU=", new String(((ByteBuffer) v).array()));

        ByteBuffer bb = ByteBuffer.wrap("weave".getBytes(StandardCharsets.UTF_8));
        v = masterFuncFactory.create("BASE64:ENCODE(ByteBuffer,String)").apply(bb);
        assertInstanceOf(String.class, v);
        assertEquals("d2VhdmU=", v);

        v = masterFuncFactory.create("BASE64:ENCODE(ByteBuffer,Bytes)").apply(bb);
        assertInstanceOf(byte[].class, v);
        assertEquals("d2VhdmU=", new String((byte[]) v));

        v = masterFuncFactory.create("BASE64:ENCODE(ByteBuffer,ByteBuffer)").apply(bb);
        assertInstanceOf(ByteBuffer.class, v);
        assertEquals("d2VhdmU=", new String(((ByteBuffer) v).array()));
    }
    

}
