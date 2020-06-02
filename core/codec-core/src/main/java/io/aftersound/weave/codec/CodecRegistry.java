package io.aftersound.weave.codec;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

public final class CodecRegistry extends Registry<String, Codec<?, ?>> {

    public <SOURCE, ENCODED> Codec<SOURCE, ENCODED> getCodec(
            final String codecSpec,
            final ActorRegistry<CodecFactory> codecFactoryRegistry) {

        return (Codec<SOURCE, ENCODED>) get(
                codecSpec,
                new Factory<String, Codec<?, ?>>() {

                    @Override
                    public Codec<?, ?> create(String codecSpec) {
                        if (codecSpec == null || codecSpec.isEmpty()) {
                            return null;
                        }
                        String codecType = codecSpec.substring(0, codecSpec.indexOf('('));
                        return codecFactoryRegistry.get(codecType).createCodec(codecSpec);
                    }
                }
        );
    }

}
