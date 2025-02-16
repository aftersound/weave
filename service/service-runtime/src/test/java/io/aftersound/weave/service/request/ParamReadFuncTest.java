package io.aftersound.weave.service.request;

import io.aftersound.func.Descriptor;
import io.aftersound.func.FuncFactory;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.util.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ParamReadFuncTest {

    private static MasterFuncFactory funcFactory;

    @BeforeClass
    public static void setup() throws Exception {
        funcFactory = MasterFuncFactory.of(ParamValueFuncFactory.class.getName());
    }

    @Test
    public void getFuncDescriptor() {
        Descriptor descriptor = funcFactory.getFuncDescriptor("PARAM:READ");
        assertNotNull(descriptor);
    }

    @Test
    public void paramReadFunc() {
        Map<String, ParamValueHolder> paramValueHolders = MapBuilder.<String, ParamValueHolder>hashMap()
                .put("firstName", ParamValueHolder.singleValued("firstName", ProtoTypes.STRING.create(), "Tesla"))
                .put("lastName", ParamValueHolder.singleValued("lastName", ProtoTypes.STRING.create(), "Nikola"))
                .put("inventions", ParamValueHolder.singleValued("inventions", ProtoTypes.LIST.create(), Arrays.asList("AC", "The Tesla Coil")))
                .build();

        Object v = funcFactory.create("PARAM:READ(firstName,lastName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(2, ((Map) v).size());
        assertEquals("Tesla", ((Map) v).get("firstName"));
        assertEquals("Nikola", ((Map) v).get("lastName"));

        v = funcFactory.create("PARAM:READ(firstName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertEquals("Tesla", ((Map) v).get("firstName"));

        v = funcFactory.create("PARAM:READ(lastName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertEquals("Nikola", ((Map) v).get("lastName"));

        v = funcFactory.create("PARAM:READ(inventions)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertTrue(((Map) v).get("inventions") instanceof List);
    }

}