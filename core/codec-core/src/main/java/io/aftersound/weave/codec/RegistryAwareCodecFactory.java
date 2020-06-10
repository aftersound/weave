package io.aftersound.weave.codec;

import io.aftersound.weave.actor.ActorRegistry;

/**
 * A {@link CodecFactory}, which creates a {@link Codec} that combines
 * several sub {@link Codec}, needs to access {@link CodecFactory} in
 * {@link ActorRegistry}
 */
public abstract class RegistryAwareCodecFactory extends CodecFactory {

    protected ActorRegistry<CodecFactory> codecFactoryRegistry;

    final void setRegistry(ActorRegistry<CodecFactory> codeFactoryRegistry) {
        this.codecFactoryRegistry = codeFactoryRegistry;
    }

}
