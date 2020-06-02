package io.aftersound.weave.codec;

/**
 * A conceptual {@link Codec} which
 *   1.encode SOURCE into string
 *   2.decode string back to SOURCE
 * @param <SOURCE>
 *          generic type of source entity
 */
public interface TextualCodec<SOURCE> extends Codec<SOURCE, String> {
}
