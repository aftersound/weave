package io.aftersound.weave.common;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.CodecFactory;

public abstract class CodecFactoryRegistryAwareValueFuncFactory extends ValueFuncFactory {

    protected ActorRegistry<CodecFactory> codecFactoryRegistry;

    final void setCodecFactoryRegistry(ActorRegistry<CodecFactory> codecFactoryRegistry) {
        this.codecFactoryRegistry = codecFactoryRegistry;
    }

}
