package io.aftersound.weave.codec;

import io.aftersound.weave.utils.Handle;

/**
 * A conceptual codec which
 *   1.encode SOURCE into ENCODED
 *   2.decode ENCODED back to SOURCE
 * @param <SOURCE>
 *          generic type of source entity
 * @param <ENCODED>
 *          generic type of entity encoded from source by this codec
 */
public interface Codec<SOURCE,ENCODED> {

    /**
     * Default instance of {@link CodecRegistry}, which manages
     * {@link Codec}s in according to code specs.
     * Application can still create instances of {@link CodecRegistry}
     * if DefaultInstance cannot fulfill the need.
     */
    public static final Handle<CodecRegistry> REGISTRY = Handle.of(
            "DefaultInstance",
            CodecRegistry.class
    ).setAndLock(new CodecRegistry());

    /**
     * @return
     *          an {@link Encoder} which implements encoding scheme of this {@link Codec}
     */
    Encoder<SOURCE,ENCODED> encoder();

    /**
     * @return
     *          an {@link Decoder} which implements decoding scheme of this {@link Codec}
     */
    Decoder<ENCODED,SOURCE> decoder();
}
