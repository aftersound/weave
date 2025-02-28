package io.aftersound.func.common;

import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void objectClassFunc() {
        Object value = masterFuncFactory.create("OBJECT:CLASS()").apply(new HashMap<>());
        assertEquals(String.class, value.getClass());
        assertEquals("java.util.HashMap", value);

        value = masterFuncFactory.create("OBJ:CLASS()").apply(new HashMap<>());
        assertEquals(String.class, value.getClass());
        assertEquals("java.util.HashMap", value);
    }

    @Test
    public void objectClassLoaderFunc() {
        Object value = masterFuncFactory.create("OBJECT:CLASS_LOADER()").apply(new CommonValueFuncTest());
        assertEquals(String.class, value.getClass());
        assertTrue(((String) value).contains("ClassLoader"));

        value = masterFuncFactory.create("OBJ:CL()").apply(new HashMap<>());
        assertEquals(String.class, value.getClass());
        assertTrue(((String) value).contains("ClassLoader"));
    }

    @Test
    public void objectDecodeFunc() {
        Object value = masterFuncFactory.create("OBJECT:DECODE()").apply(Base64.getDecoder().decode("rO0ABXQABXdlYXZl"));
        assertEquals(String.class, value.getClass());
        assertEquals("weave", value);

        value = masterFuncFactory.create("OBJECT:DESERIALIZE()").apply(Base64.getDecoder().decode("rO0ABXQABXdlYXZl"));
        assertEquals(String.class, value.getClass());
        assertEquals("weave", value);

        value = masterFuncFactory.create("OBJ:DESER()").apply(Base64.getDecoder().decode("rO0ABXQABXdlYXZl"));
        assertEquals(String.class, value.getClass());
        assertEquals("weave", value);
    }

    @Test
    public void objectHashFunc() {
        Object value = masterFuncFactory.create("OBJECT:HASH()").apply(new HashMap<>());
        assertInstanceOf(Integer.class, value);

        value = masterFuncFactory.create("OBJ:HASH()").apply(new HashMap<>());
        assertInstanceOf(Integer.class, value);
    }

    @Test
    public void objectIdFunc() {
        Object value = masterFuncFactory.create("OBJECT:ID()").apply(new HashMap<>());
        assertEquals(String.class, value.getClass());
        assertTrue(((String) value).contains("java.util.HashMap@"));

        value = masterFuncFactory.create("OBJ:ID()").apply(new HashMap<>());
        assertEquals(String.class, value.getClass());
        assertTrue(((String) value).contains("java.util.HashMap@"));
    }

    @Test
    public void objectInfoFunc() {
        Func<Object, Map<String, Object>> f = masterFuncFactory.create("OBJECT:INFO()");
        Map<String, Object> m = f.apply("Nikola Tesla");

        final String id = (String) m.get("id");
        assertTrue(id != null && id.contains("java.lang.String@"));

        final String className = (String) m.get("className");
        assertEquals("java.lang.String", className);

        final String cl = (String) m.get("classLoader");
        assertTrue(cl != null && cl.contains("ClassLoader@"));

        final Integer hashCode = (Integer) m.get("hashCode");
        assertNotNull(hashCode);
    }

    @Test
    public void objectEncodeFunc() {
        Object value = masterFuncFactory.create("OBJECT:ENCODE()").apply("weave");
        assertEquals(byte[].class, value.getClass());
        assertEquals("rO0ABXQABXdlYXZl", Base64.getEncoder().encodeToString((byte[]) value));

        value = masterFuncFactory.create("OBJECT:SERIALIZE()").apply("weave");
        assertEquals(byte[].class, value.getClass());
        assertEquals("rO0ABXQABXdlYXZl", Base64.getEncoder().encodeToString((byte[]) value));

        value = masterFuncFactory.create("OBJ:SER()").apply("weave");
        assertEquals(byte[].class, value.getClass());
        assertEquals("rO0ABXQABXdlYXZl", Base64.getEncoder().encodeToString((byte[]) value));
    }

}
