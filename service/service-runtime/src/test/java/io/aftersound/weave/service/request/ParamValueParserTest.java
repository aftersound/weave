package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ParamValueParserTest {

    @Test
    public void parse() throws Exception {
        ActorBindings<CodecControl, CodecFactory, Codec> actorBindings =
                ActorBindingsUtil.loadActorBindings(
                        Collections.singletonList(
                                "io.aftersound.weave.service.request.SimpleStringCodecFactory"
                        ),
                        CodecControl.class,
                        Codec.class,
                        false
                );

        ActorRegistry<CodecFactory> codecFactoryRegistry = new ActorFactory<>(actorBindings).createActorRegistryFromBindings(false);

        ParamValueParser paramValueParser = new ParamValueParser(codecFactoryRegistry);

        ParamField paramField = new ParamField();
        paramField.setType(ParamType.Query);
        paramField.setValueSpec("SimpleString");
        paramField.setName("p1");
        paramField.setMultiValued(true);
        ParamValueHolder pvh = paramValueParser.parse(paramField, paramField.getName(), Arrays.asList("aaa", "bbb"));
        assertNotNull(pvh.getValue());
        assertTrue(pvh.getValue() instanceof List);
        List<String> values = (List<String>) pvh.getValue();
        assertEquals(2, values.size());
        assertEquals("AAA", values.get(0));
        assertEquals("BBB", values.get(1));
    }
}