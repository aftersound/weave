package io.aftersound.weave.codec;

/**
 * A conceptual {@link Codec} which
 *   1.encode SOURCE into byte array
 *   2.decode byte array back to SOURCE
 * @param <SOURCE>
 *          generic type of source entity
 */
public interface BinaryCodec<SOURCE> extends Codec<SOURCE, byte[]> {
}
