package io.aftersound.weave.service.request;

import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParamReadFuncTest {

    @Test
    public void paramReadFunc() {

        Map<String, ParamValueHolder> paramValueHolders = MapBuilder.hashMap()
                .kv("firstName", ParamValueHolder.singleValued("firstName", "String", "Tesla"))
                .kv("lastName", ParamValueHolder.singleValued("lastName", "String", "Nikola"))
                .kv("inventions", ParamValueHolder.singleValued("inventions", "List", Arrays.asList("AC", "The Tesla Coil")))
                .build();

        Object v = new ParamReadFuncFactory().createValueFunc("PARAM:READ(firstName,lastName)").process(paramValueHolders);
        assertTrue(v instanceof Map);
        assertEquals(2, ((Map) v).size());
        assertEquals("Tesla", ((Map) v).get("firstName"));
        assertEquals("Nikola", ((Map) v).get("lastName"));

        v = new ParamReadFuncFactory().createValueFunc("PARAM:READ(firstName)").process(paramValueHolders);
        assertEquals("Tesla", v);

        v = new ParamReadFuncFactory().createValueFunc("PARAM:READ(lastName)").process(paramValueHolders);
        assertEquals("Nikola", v);

        v = new ParamReadFuncFactory().createValueFunc("PARAM:READ(inventions)").process(paramValueHolders);
        assertTrue(v instanceof List);
    }

}