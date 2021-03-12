package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncControl;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ParamValueParserTest {

    private static ActorRegistry<ValueFuncFactory> vffr;
    private static ActorRegistry<CodecFactory> cfr;

    @BeforeClass
    public static void setup() throws Exception {
        ActorBindings<ValueFuncControl, ValueFuncFactory, ValueFunc> vffBindings =
                ActorBindingsUtil.loadActorBindings(
                        Collections.singletonList(
                                "io.aftersound.weave.service.request.CaseFuncFactory"
                        ),
                        ValueFuncControl.class,
                        ValueFunc.class,
                        false
                );

        vffr = new ActorFactory<>(vffBindings).createActorRegistryFromBindings(false);

        ActorBindings<CodecControl, CodecFactory, Codec> cfBindings =
                ActorBindingsUtil.loadActorBindings(
                        Collections.singletonList(
                                "io.aftersound.weave.service.request.StringCodecFactory"
                        ),
                        CodecControl.class,
                        Codec.class,
                        false
                );
        cfr = new ActorFactory<>(cfBindings).createActorRegistryFromBindings(false);
    }

    @Test
    public void parse() {
        ParamValueParser paramValueParser = new ParamValueParser(vffr, cfr);

        ParamField paramField;
        ParamValueHolder pvh;
        List<String> values;

        paramField = new ParamField();
        paramField.setParamType(ParamType.Query);
        paramField.setType("String");
        paramField.setValueFuncSpec("CASE(UPPERCASE)");
        paramField.setName("p1");
        paramField.setMultiValued(true);
        pvh = paramValueParser.parse(paramField, paramField.getName(), Arrays.asList("aaa", "bbb"));
        assertNotNull(pvh.getValue());
        assertTrue(pvh.getValue() instanceof List);
        values = (List<String>) pvh.getValue();
        assertEquals(2, values.size());
        assertEquals("AAA", values.get(0));
        assertEquals("BBB", values.get(1));

        paramField = new ParamField();
        paramField.setParamType(ParamType.Query);
        paramField.setType("String");
        paramField.setValueFuncSpec("CASE(Capitalization)");
        paramField.setName("p1");
        paramField.setMultiValued(true);
        pvh = paramValueParser.parse(paramField, paramField.getName(), Arrays.asList("aaa", "bbb"));
        assertNotNull(pvh.getValue());
        assertTrue(pvh.getValue() instanceof List);
        values = (List<String>) pvh.getValue();
        assertEquals(2, values.size());
        assertEquals("Aaa", values.get(0));
        assertEquals("Bbb", values.get(1));
    }
}