package io.aftersound.weave.service.request;

import io.aftersound.weave.common.MasterValueFuncFactory;
import io.aftersound.weave.common.TypeEnum;
import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ParamReadFuncTest {

    @BeforeClass
    public static void setup() throws Exception {
        MasterValueFuncFactory.init(ParamValueFuncFactory.class.getName());
    }

    @Test
    public void validateValueFuncDescriptor() {
        Descriptor descriptor = MasterValueFuncFactory.getManagedValueFuncDescriptor("PARAM:READ");
        assertNotNull(descriptor);
    }

    @Test
    public void paramReadFunc() {
        Map<String, ParamValueHolder> paramValueHolders = MapBuilder.hashMap()
                .kv("firstName", ParamValueHolder.singleValued("firstName", TypeEnum.STRING.createType(), "Tesla"))
                .kv("lastName", ParamValueHolder.singleValued("lastName", TypeEnum.STRING.createType(), "Nikola"))
                .kv("inventions", ParamValueHolder.singleValued("inventions", TypeEnum.LIST.createType(), Arrays.asList("AC", "The Tesla Coil")))
                .build();

        Object v = MasterValueFuncFactory.create("PARAM:READ(firstName,lastName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(2, ((Map) v).size());
        assertEquals("Tesla", ((Map) v).get("firstName"));
        assertEquals("Nikola", ((Map) v).get("lastName"));

        v = MasterValueFuncFactory.create("PARAM:READ(firstName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertEquals("Tesla", ((Map) v).get("firstName"));

        v = MasterValueFuncFactory.create("PARAM:READ(lastName)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertEquals("Nikola", ((Map) v).get("lastName"));

        v = MasterValueFuncFactory.create("PARAM:READ(inventions)").apply(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(1, ((Map) v).size());
        assertTrue(((Map) v).get("inventions") instanceof List);
    }

}