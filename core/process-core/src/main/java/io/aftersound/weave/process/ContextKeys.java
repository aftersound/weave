package io.aftersound.weave.process;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.component.ComponentRegistry;
import org.omg.IOP.CodecFactory;

import java.util.Map;

public final class ContextKeys {

    /**
     * Key of common context object which holds variables
     */
    public static final Key<Map<String, Object>> VARIABLES = Key.of("Variables");

    /**
     * Key of common context object which is an instance of {@link ComponentRegistry}
     */
    public static final Key<ComponentRegistry> CLIENT_REGISTRY = Key.of("ComponentRegistry");

    /**
     * Key of common context object which is an instance of {@link ActorRegistry} of {@link CodecFactory}
     */
    public static final Key<ActorRegistry<CodecFactory>> CODEC_FACTORY_REGISTRY = Key.of("CodecFactoryRegistry");
}
