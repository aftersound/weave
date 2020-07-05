package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class ValueParserRegistryTest {

    @Test
    public void getValueParser() throws Exception {
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

        ValueParserRegistry vpr = new ValueParserRegistry();
        ValueParser vp1 = vpr.getValueParser("SimpleString", codecFactoryRegistry);
        assertNotNull(vp1);
        ValueParser vp2 = vpr.getValueParser("SimpleString", codecFactoryRegistry);
        assertSame(vp1, vp2);
    }
}