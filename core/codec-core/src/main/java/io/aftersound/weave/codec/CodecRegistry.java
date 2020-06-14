package io.aftersound.weave.codec;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

/**
 * A {@link Registry} which
 *  1.creates {@link Codec} in according to codec spec
 *  2.caches created {@link Codec}
 *  3.makes {@link Codec} available when codec spec is given
 */
public final class CodecRegistry extends Registry<String, Codec<?, ?>> {

    /**
     * Get {@link Codec} which acts in according to given codec spec
     *   1.If exists, simply return cached one
     *   2.If not exists, create one, cache it then return
     * @param codecSpec
     *          codec specification
     * @param codecFactoryRegistry
     *          an {@link ActorRegistry} of {@link CodecFactory}
     * @param <SOURCE>
     *          generic type of source entity
     * @param <ENCODED>
     *          generic type of entity encoded from source
     * @return
     *          a {@link Codec} which acts in according to given codec spec
     */
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
                        CodecFactory codecFactory = codecFactoryRegistry.get(codecType);
                        if (codecFactory == null) {
                            throw new CodecException("CodecFactory for " + codecType + " is not loaded or does not exist");
                        }

                        // if identified CodecFactory needs to be aware of codec factory registry
                        if (codecFactory instanceof RegistryAwareCodecFactory) {
                            ((RegistryAwareCodecFactory) codecFactory).setRegistry(codecFactoryRegistry);
                        }

                        return codecFactory.createCodec(codecSpec);
                    }
                }
        );
    }

}
